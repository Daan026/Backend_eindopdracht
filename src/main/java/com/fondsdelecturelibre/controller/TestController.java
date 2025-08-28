package com.fondsdelecturelibre.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Dit is een openbaar endpoint";
    }

    @GetMapping("/member")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    public String memberEndpoint() {
        return "Dit is een beveiligd endpoint voor leden en admins";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "Dit is een beveiligd endpoint voor admins";
    }
}
