package net.omb.photogallery;

import net.omb.photogallery.security.SpringSecurityAuditorAware;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(scanBasePackages = {"net.omb.photogallery"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
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

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
			}
		};
	}
//	private static void startDerbyNetworkServer(){
//		try {
//			NetworkServerControl server = new NetworkServerControl
//					(InetAddress.getByName("localhost"),1527);
//
//			server.start(new PrintWriter(System.out));
//		}catch (Exception e) {
//			throw new RuntimeException("Could not start derby network server" + e.getMessage());
//		}
//	}
}
