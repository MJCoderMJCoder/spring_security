package com.springboot.spring_security.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The web application is based on Spring MVC.
 * Thus you need to configure Spring MVC and set up view controllers to expose these templates.
 * Here’s a configuration class for configuring Spring MVC in the application.
 */
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    /**
     * Adds four view controllers.
     * Two of the view controllers reference the view whose name is "home" (defined in home.html),
     * and another references the view named "hello" (defined in hello.html).
     * The fourth view controller references another view named "login".
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home"); //setViewName("home")>>>>home.html
//        registry.addViewController("/").setViewName("home"); //setViewName("home")>>>>home.html
        registry.addViewController("/hello").setViewName("hello"); //setViewName("hello")>>>>hello.html
        registry.addViewController("/login").setViewName("login"); //setViewName("login")>>>>login.html
    }
}
