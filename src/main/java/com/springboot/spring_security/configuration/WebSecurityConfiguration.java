package com.springboot.spring_security.configuration;

import com.springboot.spring_security.resources.ClientResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.jaas.AbstractJaasAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Here’s a security configuration that ensures that only authenticated users can see the secret greeting:
 * <p>
 * The WebSecurityConfig class is annotated with @EnableWebSecurity to enable Spring Security’s web security support and provide the Spring MVC D.
 * It also extends WebSecurityConfigurerAdapter and overrides a couple of its methods to set some specifics of the web security configuration.
 */
@EnableWebSecurity
@Configuration
/**
 * Spring Boot attaches a special meaning to a WebSecurityConfigurer on the class that carries the @EnableOAuth2Sso annotation:
 * it uses it to configure the security filter chain that carries the OAuth2 authentication processor.
 * So all we need to do to make our home page visible is to explicitly authorizeRequests() to the home page and the static resources it contains (we also include access to the login endpoints which handle the authentication).
 * All other requests (e.g. to the /user endpoint) require authentication.
 *
 *  To make the link to Facebook we need an @EnableOAuth2Sso annotation on our main class:
 * There are 2 features behind @EnableOAuth2Sso: the OAuth2 client, and the authentication.
 */
//@EnableOAuth2Sso
/**
 *Manual Configuration of OAuth2 Client
 * The client piece is provided by Spring Security OAuth2 and switched on by a different annotation @EnableOAuth2Client.
 * So the first step in this transformation is to remove the @EnableOAuth2Sso and replace it with the lower level annotation:
 * Once that is done we have some stuff created for us that will be useful.
 * First off we can inject an OAuth2ClientContext and use it to build an authentication filter that we add to our security configuration:
 */
@EnableOAuth2Client
/**
 *Enabling the Authorization Server
 * If we want to turn our application into an OAuth2 Authorization Server, need to with some basic features (one client and the ability to create access tokens).
 * An Authorization Server is nothing more than a bunch of endpoints, and they are implemented in Spring OAuth2 as Spring MVC handlers.
 * We already have a secure application, so it’s really just a matter of adding the @EnableAuthorizationServer annotation:
 * With that @EnableAuthorizationServer annotation in place Spring Boot will install all the necessary endpoints and set up the security for them, provided we supply a few details of an OAuth2 client we want to support:
 */
@EnableAuthorizationServer
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter implements ApplicationListener<AuthenticationSuccessEvent> {

    //    inject an OAuth2ClientContext and use it to build an authentication filter （refs:ssoFilter） that we add to our security configuration:
    @Qualifier("oauth2ClientContext")
    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    /**
     * The configure(HttpSecurity) method defines which URL paths should be secured and which should not.
     * Specifically, the "/" and "/home" paths are configured to not require any authentication. All other paths must be authenticated.
     * <p>
     * When a user successfully logs in, they will be redirected to the previously requested page that required authentication.
     * There is a custom "/login" page specified by loginPage(), and everyone is allowed to view it.
     * <p>
     * /* 是拦截所有的文件夹，不包含子文件夹
     * /** 是拦截所有的文件夹及里面的子文件夹
     * <p>
     * To make the link visible we also need to switch off the security on the home page by adding a WebSecurityConfigurer:
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
        //All requests are protected by default
        http.antMatcher("/**")
                .authorizeRequests()

                //	The home page and login endpoints are explicitly excluded
//                .antMatchers("/", "/home", "/error**", "/index.html", "/webjars/**", "/login**")
                .antMatchers("/", "/unauthenticated", "/error**", "/webjars/**", "/login**")
                .permitAll()

                //All other endpoints require an authenticated user
                .anyRequest()
                .authenticated()

                //Unauthenticated users are re-directed to the home page
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))

                //There is a custom "/login" page specified by loginPage(), and everyone is allowed to view it.
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()

                //Spring Security has built in support for a /logout endpoint which will do the right thing for us (clear the session and invalidate the cookie).
                .and()
                .logout()
                .logoutSuccessUrl("/home")
                .permitAll()


//Protect the user from Cross Site Request Forgery (CSRF, pronounced "sea surf"), it requires a token to be included in the request.The value of the token is linked to the current session。
// For instance in Angular(XSRF) the front end would like the server to send it a cookie called "XSRF-TOKEN" and if it sees that, it will send the value back as a header named "X-XSRF-TOKEN".
// To teach Spring Security about this we need to add a filter that creates the cookie and also we need to tell the existing CRSF filter about the header name.
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

                //Build an authentication filter that we add to our security configuration:
                .and()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    /**
     * As for the userDetailsService() method,
     * it sets up an in-memory user store with a single user.
     * That user is given a username of "user", a password of "password", and a role of "USER".
     *
     * @return
     */
    @Override
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("123")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
//        return super.userDetailsService();
    }


    /**
     * To add an additional security filter to handle the "/login/github" requests coming from our new link.
     *
     * @return
     */
    private Filter ssoFilter() {
        OAuth2ClientAuthenticationProcessingFilter githubFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/github");
//        OAuth2RestTemplate githubTemplate = new OAuth2RestTemplate(github().getClient(), oauth2ClientContext);
        OAuth2RestTemplate githubTemplate = ServletCustomizer.oauth2RestTemplate(github().getClient(), oauth2ClientContext);
        githubFilter.setRestTemplate(githubTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(github().getResource().getUserInfoUri(), github().getClient().getClientId());
        tokenServices.setRestTemplate(githubTemplate);
        tokenServices.setAuthoritiesExtractor(ServletCustomizer.authoritiesExtractor(githubTemplate));
        githubFilter.setTokenServices(tokenServices);
        return githubFilter;
    }

    /**
     * Make is to explicitly support the redirects from our app to GitHub.
     * This is handled in Spring OAuth2 with a servlet Filter, and the filter is already available in the application context because we used @EnableOAuth2Client.
     * All that is needed is to wire the filter up so that it gets called in the right order in our Spring Boot application.
     * <p>
     * Register filter with a sufficiently low order that it comes before the main Spring Security filter
     *
     * @param filter
     * @return
     */
    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<OAuth2ClientContextFilter>();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    /**
     * Uses a new wrapper object ClientResources that consolidates the OAuth2ProtectedResourceDetails and the ResourceServerProperties that were declared as separate @Beans in the last version of the app:
     *
     * @return
     */
    @Bean
    @ConfigurationProperties("github")
    public ClientResources github() {
        return new ClientResources();
    }

    /**
     * ApplicationListener<AuthenticationSuccessEvent> 监听器：
     * 用户登录验证成功后的回调事件
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        System.out.println("登录成功：" + event);
    }

    /**
     * Note that with both these "static" data objects (github() and githubResource()) we used a @Bean decorated as @ConfigurationProperties.
     * That means that we can convert the application.yml to a slightly new format,
     * where the prefix for configuration is github instead of security.oauth2:
     * <p>
     * Filter also needs to know about the client registration with Github：
     *
     * @return
     */
//    @Bean
//    @ConfigurationProperties("github.client")
//    public AuthorizationCodeResourceDetails github() {
//        return new AuthorizationCodeResourceDetails();
//    }

    /**
     * Note that with both these "static" data objects (github() and githubResource()) we used a @Bean decorated as @ConfigurationProperties.
     * * That means that we can convert the application.yml to a slightly new format,
     * * where the prefix for configuration is github instead of security.oauth2:
     * <p>
     * To complete the authentication it needs to know where the user info endpoint is in  Github：
     *
     * @return
     */
//    @Bean
//    @ConfigurationProperties("github.resource")
//    public ResourceServerProperties githubResource() {
//        return new ResourceServerProperties();
//    }
}