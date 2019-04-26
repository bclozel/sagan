package sagan.renderer.guides;

import java.util.Arrays;

import sagan.renderer.github.Repository;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;
import org.springframework.util.StringUtils;

/**
 * A Spring Guide ("Getting started guide", "Topical guide" or "Tutorial")
 * is a document for learning Spring technologies, backed by a Github repository.
 */
@Relation(collectionRelation = "guides")
class GuideResource extends ResourceSupport {

	private String name;

	private String title;

	private String description;

	private GuideType type;

	private String[] projects;

	GuideResource(Repository repository) {
		this.name = repository.getName();
		String description = repository.getDescription();
		if (description != null) {
			String[] split = repository.getDescription().split("::");
			this.title = split[0].trim();
			this.description = (split.length > 1) ? split[1].trim() : "";
		} else {
			this.title = "";
			this.description = "";
		}
		if (repository.getTopics() != null) {
			this.projects = repository.getTopics().toArray(new String[0]);
		}
		else {
			this.projects = new String[0];
		}
		this.type = Arrays.stream(GuideType.values())
				.filter(type -> repository.getName().startsWith(type.getPrefix()))
				.findFirst().orElse(GuideType.UNKNOWN);
	}

	public String getName() {
		return this.name;
	}

	public String getTitle() {
		return this.title;
	}

	public String getDescription() {
		return this.description;
	}

	public String getType() {
		return this.type.getName();
	}

	public String[] getProjects() {
		return this.projects;
	}
}