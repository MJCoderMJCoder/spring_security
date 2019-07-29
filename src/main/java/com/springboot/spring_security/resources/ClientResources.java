package com.springboot.spring_security.resources;

import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

/**
 * Uses a new wrapper object ClientResources that consolidates the OAuth2ProtectedResourceDetails and the ResourceServerProperties that were declared as separate @Beans in the last version of the app:
 * Uses @NestedConfigurationProperty to instructs the annotation processor to crawl that type for meta-data as well since it does not represents a single value but a complete nested type.
 */
public class ClientResources {

    @NestedConfigurationProperty
    private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

    @NestedConfigurationProperty
    private ResourceServerProperties resource = new ResourceServerProperties();

    public AuthorizationCodeResourceDetails getClient() {
        return client;
    }

    public ResourceServerProperties getResource() {
        return resource;
    }
}
