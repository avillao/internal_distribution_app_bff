package com.dev_crazy.internal_distribution_app.admin_service.controller.auth;

import com.dev_crazy.internal_distribution_app.admin_service.dto.request.auth.LoginDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.response.ResponseDTO;
import com.dev_crazy.internal_distribution_app.admin_service.service.keycloak.KeycloakAuthzService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private KeycloakAuthzService keycloakAuthzService;


    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<String>> login(@Valid @ModelAttribute LoginDTO body, HttpServletResponse res) {
        ResponseDTO<String> response = new ResponseDTO<>();

        AccessTokenResponse resToken = keycloakAuthzService.getUserToken(body.getUsername(), body.getPassword());
        this.setAuthCookie(res, resToken);

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);
        response.setData(null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseDTO<String>> refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse res) {
        ResponseDTO<String> response = new ResponseDTO<>();

        AccessTokenResponse resToken = keycloakAuthzService.refreshToken(refreshToken);
        this.setAuthCookie(res, resToken);

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);
        response.setData(null);



        return ResponseEntity.ok(response);
    }

    private void setAuthCookie(HttpServletResponse res, AccessTokenResponse token){
        Cookie tokenCookie = new Cookie("access_token",  token.getToken());
        tokenCookie.setMaxAge((int) token.getExpiresIn() - 10);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");

        Cookie refreshCookie = new Cookie("refresh_token",  token.getRefreshToken());
        refreshCookie.setMaxAge((int) token.getRefreshExpiresIn() - 10);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");

        res.addCookie(tokenCookie);
        res.addCookie(refreshCookie);
    }
}
