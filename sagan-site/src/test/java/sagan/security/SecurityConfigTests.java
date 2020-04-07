package sagan.security;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Rob Winch
 */
@SecurityTest
@RunWith(SpringRunner.class)
public class SecurityConfigTests {
	@Autowired
	MockMvc mockMvc;

	@Test
	public void httpWhenProductionHeaderThenRedirectsToHttps() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/").header("x-forwarded-port", "443"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("https://localhost/"));
	}

	@Test
	public void homePageWhenAnonymousThenOk() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void adminWhenAnonymousThenSigninRequired() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/signin"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void adminWhenAdminThenOk() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@WithMockUser
	public void adminWhenUserThenForbidden() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	// this should match the link in signin.html
	public void authorizationWhenGithubThenRequestsToken() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/oauth2/authorization/github")).andDo(result -> {
			String redirectedUrl = result.getResponse().getRedirectedUrl();
			Assertions.assertThat(redirectedUrl).startsWith("https://github.com/login/oauth/authorize");
		});
	}

	@Test
	public void projectMetadataWhenGetThenNoAuthenticationRequired() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/project_metadata/spring-security"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void projectMetadataWhenHeadThenNoAuthenticationRequired() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.head("/project_metadata/spring-security"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void projectMetadataWhenPostAndAnonymousThenAuthenticationRequired() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.post("/project_metadata/spring-security"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	public void projectMetadataWhenPutAndAnonymousThenAuthenticationRequired() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.put("/project_metadata/spring-security"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	public void projectMetadataWhenDeleteAndAnonymousThenAuthenticationRequired() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/project_metadata/spring-security"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void projectMetadataWhenPostAndAdminThenForbidden() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.post("/project_metadata/spring-security"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void projectMetadataWhenPutAndAndAdminThenForbidden() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.put("/project_metadata/spring-security"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void projectMetadataWhenDeleteAndAdminThenForbidden() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/project_metadata/spring-security"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@WithMockUser(roles = {"API", "ADMIN"})
	public void projectMetadataWhenPostAndApiAdminThenOk() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.post("/project_metadata/spring-security"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@WithMockUser(roles = {"API", "ADMIN"})
	public void projectMetadataWhenPutAndAndApiAdminThenOk() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.put("/project_metadata/spring-security"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@WithMockUser(roles = {"API", "ADMIN"})
	public void projectMetadataWhenDeleteAndApiAdminThenOk() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/project_metadata/spring-security"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

}