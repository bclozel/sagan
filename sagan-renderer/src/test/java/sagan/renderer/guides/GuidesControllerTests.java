package sagan.renderer.guides;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import sagan.renderer.github.GithubClient;
import sagan.renderer.github.GithubResourceNotFoundException;
import sagan.renderer.github.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.JsonPathExpectationsHelper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link GuidesController}
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GuidesController.class)
public class GuidesControllerTests {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private GuideRenderer guideRenderer;

	@MockBean
	private GithubClient githubClient;

	@Test
	public void fetchAllGuides() throws Exception {
		Repository restService = new Repository(12L, "gs-rest-service",
				"spring-guides/gs-rest-service",
				"REST service sample :: Building a REST service",
				"http://example.org/spring-guides/gs-rest-service",
				"git://example.org/spring-guides/gs-rest-service.git",
				Arrays.asList("spring-boot", "spring-framework"));
		Repository securingWeb = new Repository(15L, "gs-securing-web",
				"spring-guides/gs-securing-web", "Securing Web :: Securing a Web Application",
				"http://example.org/spring-guides/gs-securing-web",
				"git://example.org/spring-guides/gs-securing-web.git", null);

		given(this.githubClient.fetchOrgRepositories("spring-guides"))
				.willReturn(Arrays.asList(restService, securingWeb));

		this.mvc.perform(get("/guides/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.guides[0].name").value("gs-rest-service"))
				.andExpect(jsonPath("$._embedded.guides[0].projects[0]").value("spring-boot"))
				.andExpect(hasLink("$._embedded.guides[0]._links", "self",
						"http://localhost/guides/gs-rest-service"))
				.andExpect(hasLink("$._embedded.guides[0]._links", "github",
						"http://example.org/spring-guides/gs-rest-service"))
				.andExpect(hasLink("$._embedded.guides[0]._links", "git",
						"git://example.org/spring-guides/gs-rest-service.git"))
				.andExpect(jsonPath("$._embedded.guides[1].name").value("gs-securing-web"))
				.andExpect(jsonPath("$._embedded.guides[1].projects").isEmpty())
				.andExpect(hasLink("$._embedded.guides[1]._links", "self",
						"http://localhost/guides/gs-securing-web"))
				.andExpect(hasLink("$._embedded.guides[1]._links", "github",
						"http://example.org/spring-guides/gs-securing-web"))
				.andExpect(hasLink("$._embedded.guides[1]._links", "git",
						"git://example.org/spring-guides/gs-securing-web.git"));
	}

	@Test
	public void fetchAllGuidesFiltersUnknownTypes() throws Exception {
		Repository deprecatedGuide = new Repository(15L, "deprecate-gs-device-detection",
				"spring-guides/deprecate-gs-device-detection",
				"Detecting a Device :: Learn how to use Spring to detect the type of device.",
				"http://example.org/spring-guides/deprecate-gs-device-detection",
				"git://example.org/spring-guides/deprecate-gs-device-detection.git", null);

		given(this.githubClient.fetchOrgRepositories("spring-guides"))
				.willReturn(Arrays.asList(deprecatedGuide));

		this.mvc.perform(get("/guides/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded").doesNotExist());
	}

	@Test
	public void fetchGuide() throws Exception {
		Repository restService = new Repository(12L, "gs-rest-service",
				"spring-guides/gs-rest-service",
				"REST service sample :: Building a REST service :: spring-boot,spring-framework",
				"http://example.org/spring-guides/gs-rest-service",
				"git://example.org/spring-guides/gs-rest-service.git",
				Arrays.asList("spring-boot", "spring-framework"));
		Repository securingWeb = new Repository(15L, "gs-securing-web",
				"spring-guides/gs-securing-web", "Securing Web :: Securing a Web Application",
				"http://example.org/spring-guides/gs-securing-web",
				"git://example.org/spring-guides/gs-securing-web.git", null);
		given(this.githubClient.fetchOrgRepositories("spring-guides"))
				.willReturn(Arrays.asList(restService, securingWeb));

		this.mvc.perform(get("/guides/{guide}", "gs-rest-service"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("gs-rest-service"))
				.andExpect(jsonPath("$.projects[0]").value("spring-boot"))
				.andExpect(hasLink("self", "http://localhost/guides/gs-rest-service"))
				.andExpect(hasLink("github", "http://example.org/spring-guides/gs-rest-service"))
				.andExpect(hasLink("git", "git://example.org/spring-guides/gs-rest-service.git"));
	}

	@Test
	public void fetchUnknownGuide() throws Exception {
		Repository securingWeb = new Repository(15L, "gs-securing-web",
				"spring-guides/gs-securing-web", "Securing Web :: Securing a Web Application",
				"http://example.org/spring-guides/gs-securing-web",
				"git://example.org/spring-guides/gs-securing-web.git", null);
		given(this.githubClient.fetchOrgRepositories("spring-guides"))
				.willReturn(Arrays.asList(securingWeb));

		this.mvc.perform(get("/guides/{guide}", "gs-rest-service"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void fetchGuideContent() throws Exception {
		GuideContentResource content = new GuideContentResource("gs-rest-service", "content", "toc");
		given(this.guideRenderer.render("gs-rest-service")).willReturn(content);
		this.mvc.perform(get("/guides/{guide}/content", "gs-rest-service"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.content").value("content"))
				.andExpect(jsonPath("$.tableOfContents").value("toc"))
				.andExpect(hasLink("self", "http://localhost/guides/gs-rest-service/content"))
				.andExpect(hasLink("guide", "http://localhost/guides/gs-rest-service"));
	}

	@Test
	public void fetchUnknownGuideContent() throws Exception {
		given(this.guideRenderer.render("gs-rest-service"))
				.willThrow(new GithubResourceNotFoundException("spring-guides", "gs-securing-web"));
		this.mvc.perform(get("/guides/{guide}/content", "gs-rest-service"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	static LinksMatcher hasLink(String name, String href) {
		return new LinksMatcher(name, href);
	}

	static LinksMatcher hasLink(String prefix, String name, String href) {
		return new LinksMatcher(prefix, name, href);
	}

	static class LinksMatcher implements ResultMatcher {

		private String prefix;

		private String linkName;

		private String href;

		public LinksMatcher(String linkName, String href) {
			this.linkName = linkName;
			this.href = href;
		}

		public LinksMatcher(String prefix, String linkName, String href) {
			this.prefix = prefix;
			this.linkName = linkName;
			this.href = href;
		}

		@Override
		public void match(MvcResult result) throws Exception {
			Assert.hasText(this.linkName, "The link should have a name");
			String content = result.getResponse().getContentAsString();
			if (StringUtils.isEmpty(this.prefix)) {
				this.prefix = "$._links";
			}
			String hrefExpr = this.prefix + "." + this.linkName + ".href";
			JsonPathExpectationsHelper hrefHelper = new JsonPathExpectationsHelper(hrefExpr);
			hrefHelper.assertValue(content, this.href);
		}
	}

}
