package sagan.renderer;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Sagan Renderer application
 */
@ConfigurationProperties("sagan.renderer")
public class RendererProperties {

	private final Github github = new Github();

	private final Guides guides = new Guides();

	public Github getGithub() {
		return this.github;
	}

	public Guides getGuides() {
		return this.guides;
	}

	public static class Github {

		/**
		 * Access token ("username:access_token") to query public github endpoints.
		 */
		private String token;

		public String getToken() {
			return this.token;
		}

		public void setToken(String token) {
			this.token = token;
		}

	}

	public static class Guides {

		/**
		 * Name of the Github organization to fetch guides from.
		 */
		private String organization = "spring-guides";

		public String getOrganization() {
			return this.organization;
		}

		public void setOrganization(String organization) {
			this.organization = organization;
		}
	}
}
