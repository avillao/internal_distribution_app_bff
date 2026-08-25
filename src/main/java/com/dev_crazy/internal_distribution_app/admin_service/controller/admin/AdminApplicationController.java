package com.dev_crazy.internal_distribution_app.admin_service.controller.admin;

import com.dev_crazy.internal_distribution_app.admin_service.dto.request.application.ApplicationCreateDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.request.application.ApplicationFilterDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.request.application.ApplicationUpdateDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.response.ResponseDTO;
import com.dev_crazy.internal_distribution_app.admin_service.model.Application;
import com.dev_crazy.internal_distribution_app.admin_service.service.application.IApplicationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/application")
public class AdminApplicationController {

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private ModelMapper modelMaper;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("")
    public ResponseEntity<ResponseDTO<List<Application>>> findAll(@Valid @ModelAttribute ApplicationFilterDTO inputFilters) {
        ResponseDTO<List<Application>> response = new ResponseDTO<>();

        Map<String, Object> filters = objectMapper.convertValue(inputFilters, new TypeReference<Map<String, Object>>(){});
        filters.values().removeAll(Collections.singleton(null));
        List<Application> applicationList = applicationService.findAll(filters);

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);
        response.setData(applicationList);

        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody ApplicationCreateDTO body) {
        ResponseDTO<String> response = new ResponseDTO<>();
        Application application = modelMaper.map(body, Application.class);
        applicationService.create(application);
        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{applicationCode}")
    public ResponseEntity<ResponseDTO<Application>> findByCode(@PathVariable String applicationCode) {
        ResponseDTO<Application> response = new ResponseDTO<>();

        Application application = applicationService.findByCode(applicationCode);

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);
        response.setData(application);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{applicationCode}")
    public ResponseEntity<ResponseDTO<String>> update(@PathVariable String applicationCode, @Valid @RequestBody ApplicationUpdateDTO body) {
        ResponseDTO<String> response = new ResponseDTO<>();
        Application application = modelMaper.map(body, Application.class);
        applicationService.update(applicationCode, application);
        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);

        return ResponseEntity.ok(response);
    }
}
