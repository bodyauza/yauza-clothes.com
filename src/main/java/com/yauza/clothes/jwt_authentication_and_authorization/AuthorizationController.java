package com.yauza.clothes.jwt_authentication_and_authorization;

import com.yauza.clothes.jwt_authentication_and_authorization.domain.JwtAuthentication;
import com.yauza.clothes.jwt_authentication_and_authorization.jwt_service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthorizationController {

    @Autowired
    private AuthenticationService authService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/hello/user")
    public String helloUser(Model model) {
        final JwtAuthentication authInfo = authService.getAuthInfo();
        model.addAttribute("username", authInfo.getPrincipal());
        return "private_office";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/hello/admin")
    public String helloAdmin(Model model) {
        final JwtAuthentication authInfo = authService.getAuthInfo();
        model.addAttribute("username", authInfo.getPrincipal());
        return "private_office_admin";
    }

}
