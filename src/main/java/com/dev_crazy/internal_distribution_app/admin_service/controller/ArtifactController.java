package com.dev_crazy.internal_distribution_app.admin_service.controller;

import com.dev_crazy.internal_distribution_app.admin_service.dto.request.application.ApplicationFilterDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.request.artifact.ArtifactCreateDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.request.artifact.ArtifactFilterDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.response.ResponseDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.response.artifact.ArtifactInfoDTO;
import com.dev_crazy.internal_distribution_app.admin_service.model.Application;
import com.dev_crazy.internal_distribution_app.admin_service.model.Artifact;
import com.dev_crazy.internal_distribution_app.admin_service.service.artifact.ArtifactService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
public class ArtifactController {
    @Autowired
    private ModelMapper modelMaper;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ArtifactService artifactService;

    @PostMapping("/artifact")
    private ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody ArtifactCreateDTO body) {
        ResponseDTO<String> response = new ResponseDTO<>();
        Artifact artifact = modelMaper.map(body, Artifact.class);
        artifactService.create(artifact);

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);

        return ResponseEntity.ok(response);
    }

    @GetMapping("resource-application/{resourceApplicationCode}/artifact")
    private ResponseEntity<ResponseDTO<List<ArtifactInfoDTO>>> findAll(@PathVariable String resourceApplicationCode, @Valid @ModelAttribute ArtifactFilterDTO inputFilters) {
        ResponseDTO<List<ArtifactInfoDTO>> response = new ResponseDTO<>();

        Map<String, Object> filters = objectMapper.convertValue(inputFilters, new TypeReference<Map<String, Object>>(){});
        filters.values().removeAll(Collections.singleton(null));

        List<Artifact> artifacts = artifactService.findAll(resourceApplicationCode, filters);
        List<ArtifactInfoDTO> artifactInfoDTOS = modelMaper.map(artifacts, new TypeToken<List<ArtifactInfoDTO>>() {}.getType());

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);
        response.setData(artifactInfoDTOS);

        return ResponseEntity.ok(response);
    }

    @GetMapping("resource-application/{resourceApplicationCode}/artifact/{artifactCode}")
    private ResponseEntity<ResponseDTO<Artifact>> findByCode(@PathVariable String resourceApplicationCode, @PathVariable String artifactCode) {
        ResponseDTO<Artifact> response = new ResponseDTO<>();

        Artifact artifact = artifactService.findByCode(resourceApplicationCode, artifactCode);

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);
        response.setData(artifact);

        return ResponseEntity.ok(response);
    }

    @GetMapping("resource-application/{resourceApplicationCode}/artifact/latest")
    private ResponseEntity<ResponseDTO<ArtifactInfoDTO>> findLatest(@PathVariable String resourceApplicationCode) {
        ResponseDTO<ArtifactInfoDTO> response = new ResponseDTO<>();

        Artifact artifact = artifactService.findLatest(resourceApplicationCode);
        ArtifactInfoDTO artifactInfoDTO = modelMaper.map(artifact, ArtifactInfoDTO.class);

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);
        response.setData(artifactInfoDTO);

        return ResponseEntity.ok(response);
    }
}
