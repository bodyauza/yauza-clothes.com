package com.hood.merch.controllers;

import com.hood.merch.security.domain.JwtAuthentication;
import com.hood.merch.security.service_auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("person")
public class HelloController {

    @Autowired
    private AuthService authService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("hello/user")
    public ResponseEntity<String> helloUser(Model model , HttpServletRequest request) {
        model.addAttribute("title", "Личный кабинет");
        String tokenInRequest = request.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", tokenInRequest);

        return new ResponseEntity<>("private", headers, HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("hello/admin")
    public ResponseEntity<String> helloAdmin(Model model , HttpServletRequest request) {
        model.addAttribute("title", "Личный кабинет");
        String tokenInRequest = request.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", tokenInRequest);

        return new ResponseEntity<>("private", headers, HttpStatus.OK);
    }
}
