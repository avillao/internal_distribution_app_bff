package com.dev_crazy.internal_distribution_app.admin_service.controller.auth;

import com.dev_crazy.internal_distribution_app.admin_service.dto.request.application.ApplicationCreateDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.request.auth.LoginDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.response.ResponseDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.response.auth.LoginTokenDTO;
import com.dev_crazy.internal_distribution_app.admin_service.model.Application;
import com.dev_crazy.internal_distribution_app.admin_service.service.keycloak.KeycloakOpenIdService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
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

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<LoginTokenDTO>> login(@Valid @ModelAttribute LoginDTO body, HttpServletResponse res) {
        ResponseDTO<LoginTokenDTO> response = new ResponseDTO<>();

        Map<String, Object> keycloakTokenMap = keycloakOpenIdService.getUserToken(body.getUsername(), body.getPassword());
        LoginTokenDTO tokenData = modelMapper.map(keycloakTokenMap, LoginTokenDTO.class);

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);
        response.setData(tokenData);

        return ResponseEntity.ok(response);
    }
}
