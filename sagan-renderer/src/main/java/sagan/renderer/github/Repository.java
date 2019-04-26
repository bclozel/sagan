package sagan.renderer.github;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Github repository information
 */
public class Repository {

	private Long id;

	private String name;

	private String fullName;

	private String description;

	private String htmlUrl;

	private String gitUrl;

	private List<String> topics;

	@JsonCreator
	public Repository(@JsonProperty("id") Long id, @JsonProperty("name") String name,
			@JsonProperty("full_name") String fullName, @JsonProperty("description") String description,
			@JsonProperty("html_url") String htmlUrl, @JsonProperty("git_url") String gitUrl,
			@JsonProperty("topics") List<String> topics) {
		this.name = name;
		this.fullName = fullName;
		this.description = description;
		this.htmlUrl = htmlUrl;
		this.gitUrl = gitUrl;
		this.topics = topics;
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getFullName() {
		return this.fullName;
	}

	public String getDescription() {
		return this.description;
	}

	public String getHtmlUrl() {
		return this.htmlUrl;
	}

	public String getGitUrl() {
		return this.gitUrl;
	}

	public List<String> getTopics() {
		return topics;
	}
}
