package com.dev_crazy.internal_distribution_app.admin_service.controller.user;

import com.dev_crazy.internal_distribution_app.admin_service.dto.response.ResponseDTO;
import com.dev_crazy.internal_distribution_app.admin_service.model.Application;
import com.dev_crazy.internal_distribution_app.admin_service.model.ApplicationDetail;
import com.dev_crazy.internal_distribution_app.admin_service.service.application.IApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{application_code}/detail")
    public ResponseEntity<ResponseDTO<ApplicationDetail>> detail(@PathVariable("application_code") String applicationCode) {
        ResponseDTO<ApplicationDetail> response = new ResponseDTO<>();

        ApplicationDetail applicationDetail = applicationService.getApplicationDetail(applicationCode);

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);
        response.setData(applicationDetail);

        return ResponseEntity.ok(response);
    }
}
