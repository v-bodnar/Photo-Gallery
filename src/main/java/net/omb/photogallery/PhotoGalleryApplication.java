package net.omb.photogallery;

import net.omb.photogallery.security.SpringSecurityAuditorAware;
import org.apache.derby.drda.NetworkServerControl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

import java.io.PrintWriter;
import java.net.InetAddress;

@SpringBootApplication(scanBasePackages = {"net.omb.photogallery"})
public class PhotoGalleryApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(PhotoGalleryApplication.class);
	}

	public static void main(String[] args) {
		//startDerbyNetworkServer();
		SpringApplication.run(PhotoGalleryApplication.class, args);
	}
	@Bean
	public AuditorAware<String> myAuditorProvider() {
		return new SpringSecurityAuditorAware();
	}

	private static void startDerbyNetworkServer(){
		try {
			NetworkServerControl server = new NetworkServerControl
					(InetAddress.getByName("localhost"),1527);

			server.start(new PrintWriter(System.out));
		}catch (Exception e) {
			throw new RuntimeException("Could not start derby network server" + e.getMessage());
		}
	}
}
