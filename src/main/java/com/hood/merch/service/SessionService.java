package com.hood.merch.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class SessionService {

    public void removeSessions(HttpServletRequest request, HttpServletResponse response) {
        List<String> attribs = Arrays.asList("item", "item1", "item2", "item3", "item4", "item5");
        HttpSession session = request.getSession();

        for (String attr : attribs) {
            session.removeAttribute(attr);
        }
    }

}
