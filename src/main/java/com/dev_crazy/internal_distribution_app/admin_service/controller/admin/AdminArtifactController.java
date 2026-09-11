package com.dev_crazy.internal_distribution_app.admin_service.controller.admin;

import com.dev_crazy.internal_distribution_app.admin_service.dto.request.artifact.ArtifactCreateDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.request.artifact.ArtifactFilterDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.response.ResponseDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.response.artifact.ArtifactInfoDTO;
import com.dev_crazy.internal_distribution_app.admin_service.exception.BaseServiceException;
import com.dev_crazy.internal_distribution_app.admin_service.model.Artifact;
import com.dev_crazy.internal_distribution_app.admin_service.model.BinaryDetail;
import com.dev_crazy.internal_distribution_app.admin_service.model.Platform;
import com.dev_crazy.internal_distribution_app.admin_service.service.artifact.ArtifactService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminArtifactController {
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

    @GetMapping("application/{resourceApplicationCode}/artifact")
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

    @GetMapping("application/{resourceApplicationCode}/artifact/{artifactCode}")
    private ResponseEntity<ResponseDTO<ArtifactInfoDTO>> findByCode(@PathVariable String resourceApplicationCode, @PathVariable String artifactCode) {
        ResponseDTO<ArtifactInfoDTO> response = new ResponseDTO<>();

        Artifact artifact = artifactService.findByCode(resourceApplicationCode, artifactCode);
        ArtifactInfoDTO artifactInfoDTO = modelMaper.map(artifact, ArtifactInfoDTO.class);

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);
        response.setData(artifactInfoDTO);

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value="application/{resourceApplicationCode}/artifact/{artifactCode}",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }
    )
    private ResponseEntity<ResponseDTO<String>> saveBinary(
            @PathVariable String resourceApplicationCode,
            @PathVariable String artifactCode,
            @RequestParam("file") MultipartFile file
    ) {
        ResponseDTO<String> response = new ResponseDTO<>();

        if (file.isEmpty()) {
            throw new BaseServiceException("El archivo está vacio", 400, null);
        }

        String contentType = file.getContentType();

        if(!contentType.equalsIgnoreCase("application/octet-stream") &&
                !contentType.equalsIgnoreCase("application/x-itunes-ipa") &&
                !contentType.equalsIgnoreCase("application/vnd.android.package-archive")
        ){
            throw new BaseServiceException("Tipo de archivo no permitido", 400, null);
        }

        BinaryDetail binaryDetail = new BinaryDetail();
        binaryDetail.setType(contentType);
        binaryDetail.setFilesize(file.getSize());
        binaryDetail.setFilename(file.getOriginalFilename());

        try {
            artifactService.saveBinary(binaryDetail, file.getInputStream(), resourceApplicationCode, artifactCode);
        }catch (Exception e) {
            throw new BaseServiceException(e.getMessage(), 400, e);
        }

        response.setError(false);
        response.setMessage("OK");
        response.setStatus(200);
        response.setData(null);

        return ResponseEntity.ok(response);
    }
}
