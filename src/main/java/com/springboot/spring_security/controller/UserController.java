package com.springboot.spring_security.controller;

import com.springboot.spring_security.configuration.ServletCustomizer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.security.util.Debug;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class UserController {
    /**
     * The "home" controller needs an endpoint at "/user" that describes the currently authenticated user.
     * <p>
     * It’s not a great idea to return a whole Principal in a /user endpoint like that (it might contain information you would rather not reveal to a browser client).
     * We only did it to get something working quickly. Later in the guide we will convert the endpoint to hide the information we don’t need the browser to have.
     *
     * @param principal
     * @return
     */
    @RequestMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }

    /**
     * Protecting the User Info Endpoint：
     * To use our new Authorization Server for single sign on,
     * it needs to have a /me endpoint that is protected by the access tokens it creates.
     * <p>
     * We have converted the Principal into a Map so as to hide the parts that we don’t want to expose to the browser,
     * and also to unfify the behaviour of the endpoint between the two external authentication providers.
     * In principle we could add more detail here.
     *
     * @param principal
     * @return
     */
    @RequestMapping({"/myuser", "/me"})
    public Map<String, String> me(Principal principal) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", principal.getName());
        return map;
    }
}
