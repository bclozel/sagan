package sagan;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * The entry point for the Sagan web application.
 */
@SpringBootApplication
@EnableConfigurationProperties(SiteProperties.class)
public class SiteApplication {

	public static void main(String[] args) {
		new SaganApplication(SiteApplication.class).run(args);
	}

}
