package sagan.renderer.guides;

import java.util.List;

import org.springframework.hateoas.ResourceSupport;

/**
 * Spring guide content holder.
 */
public class GuideContentResource extends ResourceSupport {

	private String repositoryName;

	private String tableOfContents;

	private String content;

	private String pushToPwsMetadata;

	private List<GuideImage> images;

	GuideContentResource(String repositoryName, String content, String tableOfContents) {
		this.repositoryName = repositoryName;
		this.content = content;
		this.tableOfContents = tableOfContents;
	}

	GuideContentResource() { }

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getTableOfContents() {
		return tableOfContents;
	}

	public void setTableOfContents(String tableOfContents) {
		this.tableOfContents = tableOfContents;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPushToPwsMetadata() {
		return pushToPwsMetadata;
	}

	public void setPushToPwsMetadata(String pushToPwsMetadata) {
		this.pushToPwsMetadata = pushToPwsMetadata;
	}

	public List<GuideImage> getImages() {
		return images;
	}

	public void setImages(List<GuideImage> images) {
		this.images = images;
	}

}