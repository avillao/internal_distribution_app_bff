package com.dev_crazy.internal_distribution_app.admin_service.controller.auth;

import com.dev_crazy.internal_distribution_app.admin_service.dto.request.application.ApplicationCreateDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.request.auth.LoginDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.response.ResponseDTO;
import com.dev_crazy.internal_distribution_app.admin_service.model.Application;
import com.dev_crazy.internal_distribution_app.admin_service.service.keycloak.KeycloakOpenIdService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private KeycloakOpenIdService keycloakOpenIdService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<String>> login(@Valid @ModelAttribute LoginDTO body, HttpServletResponse res) {
        ResponseDTO<String> response = new ResponseDTO<>();

        Map<String, Object> keycloakTokenMap = keycloakOpenIdService.getUserToken(body.getUsername(), body.getPassword());

        Cookie tokenCookie = new Cookie("access_token",  keycloakTokenMap.get("access_token").toString());
        tokenCookie.setMaxAge(Integer.parseInt(keycloakTokenMap.get("expires_in").toString()) - 10);
        tokenCookie.setHttpOnly(true);

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);
        response.setData(null);
        res.addCookie(tokenCookie);

        return ResponseEntity.ok(response);
    }
}
