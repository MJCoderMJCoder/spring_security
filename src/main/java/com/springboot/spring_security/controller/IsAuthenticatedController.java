package com.springboot.spring_security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.security.util.Debug;

/**
 * @Controller (not a @ RestController) so it can handle the redirect.
 */
@Controller
public class IsAuthenticatedController {

    /**
     * capture an authentication error and redirect to the home page with that flag set in query parameters.
     *
     * @return
     */
    @RequestMapping("/unauthenticated")
    public String unauthenticated() {
        Debug.println("container", "unauthenticated：redirect:/?error=true");
        return "redirect:/?error=true";
    }
}
