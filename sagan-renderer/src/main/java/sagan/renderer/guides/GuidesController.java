package sagan.renderer.guides;

import java.util.List;
import java.util.stream.Collectors;

import sagan.renderer.RendererProperties;
import sagan.renderer.github.GithubClient;
import sagan.renderer.github.GithubResourceNotFoundException;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * API for listing guides repositories and rendering them as {@link GuideContentResource}
 */
@RestController
@RequestMapping(path = "/guides", produces = MediaTypes.HAL_JSON_VALUE)
public class GuidesController {

	private final GuideRenderer guideRenderer;

	private final GithubClient githubClient;

	private final RendererProperties properties;

	private final GuideResourceAssembler guideResourceAssembler = new GuideResourceAssembler();

	public GuidesController(GuideRenderer guideRenderer, GithubClient github,
			RendererProperties properties) {
		this.guideRenderer = guideRenderer;
		this.githubClient = github;
		this.properties = properties;
	}

	@ExceptionHandler(GithubResourceNotFoundException.class)
	public ResponseEntity resourceNotFound() {
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/")
	public Resources<GuideResource> listGuides() {
		List<GuideResource> guideResources = this.guideResourceAssembler
				.toResources(this.githubClient.fetchOrgRepositories(properties.getGuides().getOrganization()))
				.stream().filter(guide -> !guide.getType().equalsIgnoreCase(GuideType.UNKNOWN.getName()))
				.collect(Collectors.toList());
		return new Resources<>(guideResources);
	}

	@GetMapping("/{guide}")
	public ResponseEntity<GuideResource> showGuide(@PathVariable String guide) {
		return this.githubClient.fetchOrgRepositories(properties.getGuides().getOrganization())
				.stream().filter(repository -> repository.getName().equals(guide))
				.map(this.guideResourceAssembler::toResource)
				.filter(guideResource -> !guideResource.getType().equalsIgnoreCase(GuideType.UNKNOWN.getName()))
				.map(ResponseEntity::ok)
				.findFirst().orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/{guide}/content")
	public ResponseEntity<GuideContentResource> render(@PathVariable String guide) {
		GuideContentResource guideContentResource = this.guideRenderer.render(guide);
		guideContentResource.add(linkTo(methodOn(GuidesController.class).render(guide)).withSelfRel());
		guideContentResource.add(linkTo(methodOn(GuidesController.class).showGuide(guide)).withRel("guide"));
		return ResponseEntity.ok(guideContentResource);
	}

}