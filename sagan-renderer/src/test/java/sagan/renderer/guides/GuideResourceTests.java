package sagan.renderer.guides;

import java.util.Arrays;

import org.junit.Test;
import sagan.renderer.github.Repository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link GuideResource}
 */
public class GuideResourceTests {

	@Test
	public void nullRepositoryDescription() {
		Repository repository = new Repository(12L, "gs-sample-guide",
				"spring-guides/gs-sample-quide", null,
				"http://example.org/gs-sample-guide.html", "git://example.org/gs-sample-guide.git", null);
		GuideResource guideResource = new GuideResource(repository);
		assertThat(guideResource.getTitle()).isEmpty();
		assertThat(guideResource.getDescription()).isEmpty();
		assertThat(guideResource.getType()).isEqualTo(GuideType.GETTING_STARTED.getName());
		assertThat(guideResource.getProjects()).isEmpty();
	}

	@Test
	public void noGuideProjects() {
		Repository repository = new Repository(12L, "tut-sample-guide",
				"spring-guides/tut-sample-quide", "Title :: Description",
				"http://example.org/tut-sample-guide.html", "git://example.org/tut-sample-guide.git", null);
		GuideResource guideResource = new GuideResource(repository);
		assertThat(guideResource.getTitle()).isEqualTo("Title");
		assertThat(guideResource.getDescription()).isEqualTo("Description");
		assertThat(guideResource.getType()).isEqualTo(GuideType.TUTORIAL.getName());
		assertThat(guideResource.getProjects()).isEmpty();
	}

	@Test
	public void withGuideProjects() {
		Repository repository = new Repository(12L, "top-sample-guide",
				"spring-guides/gs-sample-quide",
				"Title :: Description",
				"http://example.org/top-sample-guide.html", "git://example.org/top-sample-guide.git",
				Arrays.asList("spring-framework", "spring-boot"));
		GuideResource guideResource = new GuideResource(repository);
		assertThat(guideResource.getTitle()).isEqualTo("Title");
		assertThat(guideResource.getDescription()).isEqualTo("Description");
		assertThat(guideResource.getType()).isEqualTo(GuideType.TOPICAL.getName());
		assertThat(guideResource.getProjects()).contains("spring-framework", "spring-boot");
	}

	@Test
	public void deprecatedGuide() {
		Repository repository = new Repository(12L, "deprecated-gs-sample-guide",
				"spring-guides/deprecated-gs-sample-quide",
				"Title :: Description",
				"http://example.org/deprecated-gs-sample-guide.html",
				"git://example.org/deprecated-gs-sample-guide.git",
				Arrays.asList("spring-framework", "spring-boot"));
		GuideResource guideResource = new GuideResource(repository);
		assertThat(guideResource.getTitle()).isEqualTo("Title");
		assertThat(guideResource.getDescription()).isEqualTo("Description");
		assertThat(guideResource.getType()).isEqualTo(GuideType.UNKNOWN.getName());
		assertThat(guideResource.getProjects()).contains("spring-framework", "spring-boot");
	}

}
