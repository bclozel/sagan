package sagan.renderer.github;

public class GithubResourceNotFoundException extends RuntimeException {

	private final String resourceName;

	public GithubResourceNotFoundException(String orgName, String repositoryName) {
		super("Could not find github repository [" + orgName + "/" + repositoryName + "]");
		this.resourceName= "Repository [" + orgName + "/" + repositoryName + "]";
	}

	public GithubResourceNotFoundException(String orgName) {
		super("Could not fing github organization [" + orgName + "]");
		this.resourceName = "Organization [" + orgName + "]";
	}

	public String getResourceName() {
		return this.resourceName;
	}
}
