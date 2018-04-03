package io.pivotal.pal.initializr;

import io.pivotal.pal.initializr.custom.PalMainController;
import io.pivotal.pal.initializr.custom.PalProjectGenerator;
import io.spring.initializr.generator.ProjectGenerator;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.util.TemplateRenderer;
import io.spring.initializr.web.project.MainController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

@SpringBootApplication
public class InitializrApplication {

    public static void main(String[] args) {
        SpringApplication.run(InitializrApplication.class, args);
    }

    @Configuration
    static class InitializrConfiguration {

        @Bean
        public ProjectGenerator palProjectGenerator() {
            return new PalProjectGenerator();
        }

        @Bean
        public MainController palMainController(
                InitializrMetadataProvider metadataProvider,
                TemplateRenderer templateRenderer,
                ResourceUrlProvider resourceUrlProvider,
                ProjectGenerator projectGenerator,
                DependencyMetadataProvider dependencyMetadataProvider) {
            return new PalMainController(
                    metadataProvider,
                    templateRenderer,
                    resourceUrlProvider,
                    projectGenerator,
                    dependencyMetadataProvider);
        }
    }
}
