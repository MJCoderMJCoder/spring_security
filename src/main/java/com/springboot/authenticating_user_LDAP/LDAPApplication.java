package com.springboot.authenticating_user_LDAP;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;


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
public class LDAPApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(LDAPApplication.class)
                .properties("spring.config.name=ldap").run(args);
    }
}
