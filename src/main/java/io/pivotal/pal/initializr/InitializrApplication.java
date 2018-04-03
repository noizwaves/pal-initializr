package io.pivotal.pal.initializr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class InitializrApplication {

    public static void main(String[] args) {
        SpringApplication.run(InitializrApplication.class, args);
    }

    @Configuration
    static class InitializrConfiguration {
    }
}
