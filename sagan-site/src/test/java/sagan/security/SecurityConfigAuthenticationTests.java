package sagan.security;

import java.net.URLDecoder;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Rob Winch
 */
@RunWith(SpringRunner.class)
@SecurityTest
public class SecurityConfigAuthenticationTests {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> responseClient;

	@MockBean
	OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

	@MockBean
	ClientRegistrationRepository oauthClients;

	@Test
	public void projectMetadataWhenAuthenticateThenOk() throws Exception {
		Mockito.when(this.oauthClients.findByRegistrationId(ArgumentMatchers.any())).thenReturn(TestClientRegistrations.clientRegistration().build());
		DefaultOAuth2User user = new DefaultOAuth2User(
				AuthorityUtils.createAuthorityList("ROLE_ADMIN"),
				Collections.singletonMap("user", "octocat"),
				"user");
		Mockito.when(this.userService.loadUser(ArgumentMatchers.any())).thenReturn(user);

		this.mockMvc.perform(MockMvcRequestBuilders.post("/project_metadata/spring-security").with(SecurityMockMvcRequestPostProcessors.httpBasic("token", "")))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void projectMetadataWhenFailAuthenticateThenForbidden() throws Exception {
		Mockito.when(this.oauthClients.findByRegistrationId(ArgumentMatchers.any())).thenReturn(TestClientRegistrations.clientRegistration().build());
		Mockito.when(this.userService.loadUser(ArgumentMatchers.any())).thenThrow(new OAuth2AuthenticationException(new OAuth2Error("invalid_token")));
		this.mockMvc.perform(MockMvcRequestBuilders.post("/project_metadata/spring-security").with(SecurityMockMvcRequestPostProcessors.httpBasic("token", "")))
				// we do not want to trigger basic authentication prompt as this can enable CSRF attacks
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	public void oauth2LoginWhenSuccessThenAuthenticated() throws Exception {
		ClientRegistration registration = TestClientRegistrations.clientRegistration()
				.registrationId("github")
				.redirectUriTemplate("{baseUrl}/{action}/oauth2/code/{registrationId}")
				.build();
		Mockito.when(this.oauthClients.findByRegistrationId(ArgumentMatchers.any())).thenReturn(registration);
		DefaultOAuth2User user = new DefaultOAuth2User(
				AuthorityUtils.createAuthorityList("ROLE_ADMIN"),
				Collections.singletonMap("user", "octocat"),
				"user");
		Mockito.when(this.userService.loadUser(ArgumentMatchers.any())).thenReturn(user);
		OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse
				.withToken("123")
				.tokenType(OAuth2AccessToken.TokenType.BEARER)
				.build();
		Mockito.when(this.responseClient.getTokenResponse(ArgumentMatchers.any())).thenReturn(tokenResponse);
		MockHttpSession session = new MockHttpSession();

		MvcResult oauth2RequestResult = this.mockMvc
				.perform(MockMvcRequestBuilders.get("/oauth2/authorization/github").session(session))
				.andReturn();

		String redirectUrl = oauth2RequestResult.getResponse().getRedirectedUrl();

		String state = URLDecoder.decode(UriComponentsBuilder.fromHttpUrl(redirectUrl).build(true).getQueryParams().getFirst("state"),
				"UTF-8");
		this.mockMvc.perform(MockMvcRequestBuilders.get("/login/oauth2/code/github?code=zzbb3fRaa44&state={state}", state).session(session))
				.andExpect(SecurityMockMvcResultMatchers.authenticated())
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
	}
}