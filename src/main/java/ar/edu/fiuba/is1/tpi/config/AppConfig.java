package ar.edu.fiuba.is1.tpi.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    Path docs = Path.of("docs").toAbsolutePath().normalize();
    if (Files.isDirectory(docs)) {
      String location = docs.toUri().toString();
      if (!location.endsWith("/")) {
        location = location + "/";
      }
      registry.addResourceHandler("/docs/**").addResourceLocations(location);
    }
  }
}
