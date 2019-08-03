package com.springboot.spring_security_client;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


/**
 * @SpringBootApplication is a convenience annotation that adds all of the following:
 * @Configuration tags the class as a source of bean definitions for the application context.
 * @EnableAutoConfiguration tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
 * <p>
 * Normally you would add @EnableWebMvc for a Spring MVC app, but Spring Boot adds it automatically when it sees spring-webmvc on the classpath.
 * This flags the application as a web application and activates key behaviors such as setting up a DispatcherServlet.
 * @ComponentScan tells Spring to look for other components, configurations, and services in the hello package, allowing it to find the controllers.
 * <p>
 * The main() method uses Spring Boot’s SpringApplication.run() method to launch an application.
 */
@EnableAutoConfiguration
@Configuration
@EnableOAuth2Sso
@RestController
public class ClientApplication {

    @RequestMapping("/")
    public String home(Principal user) {
        return "Hello " + user.getName();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(ClientApplication.class)
                .properties("spring.config.name=client").run(args);
    }
}
