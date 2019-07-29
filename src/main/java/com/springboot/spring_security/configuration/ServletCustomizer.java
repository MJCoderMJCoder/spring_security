package com.springboot.spring_security.configuration;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import sun.security.util.Debug;

import java.util.List;
import java.util.Map;

@Configuration
public class ServletCustomizer {

    /**
     * A mapping from an unauthenticated response (HTTP 401, a.k.a. UNAUTHORIZED) to the "/unauthenticated" endpoint
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> customizer() {
        return container -> {
            Debug.println("container", container + "");
            container.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, "/unauthenticated"));
            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/home"));
        };
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public static OAuth2RestTemplate oauth2RestTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
        return new OAuth2RestTemplate(resource, context);
    }

    /**
     * To spice things up a bit we will extend the authentication rule to reject users that are not in the right organization.
     * It is easy to use the Github API to find out more about the user, so we just need to plug that into the right part of the authentication process.
     * Fortunately, for such a simple use case, Spring Boot has provided an easy extension point:
     * if we declare a @Bean of type AuthoritiesExtractor it will be used to construct the authorities (typically "roles") of an authenticated user.
     * We can use that hook to assert the the user is in the correct orignization, and throw an exception if not.
     *
     * @param oAuth2RestOperations
     * @return
     * @Bean注释配合@EnableOAuth2Sso来实现自动构建
     */
    @Bean
    public static AuthoritiesExtractor authoritiesExtractor(OAuth2RestOperations oAuth2RestOperations) {
        return map -> {
            String url = (String) map.get("organizations_url");
            List<Map<String, Object>> orgs = oAuth2RestOperations.getForObject(url, List.class);
            if (orgs.size() > 0 && orgs.stream().anyMatch(org -> "spring-projects".equals(org.get("login")))) {
                return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
            }
            throw new Custom401Exceprion("Not in Spring Team");
        };
    }

    public static class Custom401Exceprion extends BadCredentialsException {

        public Custom401Exceprion(String msg) {
            super(msg);
            Debug.println("Custom401Exceprion", msg + "");
        }

        public Custom401Exceprion(String msg, Throwable t) {
            super(msg, t);
            Debug.println("Custom401Exceprion", msg + "；" + t);
        }
    }
}
