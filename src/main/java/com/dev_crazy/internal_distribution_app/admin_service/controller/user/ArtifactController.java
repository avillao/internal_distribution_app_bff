package com.dev_crazy.internal_distribution_app.admin_service.controller.user;

import com.dev_crazy.internal_distribution_app.admin_service.dto.response.ResponseDTO;
import com.dev_crazy.internal_distribution_app.admin_service.dto.response.artifact.ArtifactInfoDTO;
import com.dev_crazy.internal_distribution_app.admin_service.model.Artifact;
import com.dev_crazy.internal_distribution_app.admin_service.service.artifact.ArtifactService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/application")
public class ArtifactController {

    @Autowired
    private ModelMapper modelMaper;

    @Autowired
    private ArtifactService artifactService;

    @GetMapping("{resourceApplicationCode}/artifact/latest")
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
