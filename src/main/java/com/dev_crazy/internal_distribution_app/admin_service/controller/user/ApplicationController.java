package com.dev_crazy.internal_distribution_app.admin_service.controller.user;

import com.dev_crazy.internal_distribution_app.admin_service.dto.request.application.ApplicationFilterDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.response.ResponseDTO;
import com.dev_crazy.internal_distribution_app.admin_service.model.Application;
import com.dev_crazy.internal_distribution_app.admin_service.service.application.IApplicationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/application")
public class ApplicationController {
    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private ModelMapper modelMaper;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("")
    public ResponseEntity<ResponseDTO<List<Application>>> findAll() {
        ResponseDTO<List<Application>> response = new ResponseDTO<>();

        Map<String, Object> filters = new HashMap<>();
        filters.put("enabled", true);

        List<Application> applicationList = applicationService.findAll(filters);

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);
        response.setData(applicationList);

        return ResponseEntity.ok(response);
    }
}
