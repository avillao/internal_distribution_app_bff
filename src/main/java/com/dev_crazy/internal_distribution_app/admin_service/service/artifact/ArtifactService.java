package com.dev_crazy.internal_distribution_app.admin_service.service.artifact;

import com.dev_crazy.internal_distribution_app.admin_service.exception.artifact.ArtifactNotFoundException;
import com.dev_crazy.internal_distribution_app.admin_service.exception.artifact.ArtifactVersionException;
import com.dev_crazy.internal_distribution_app.admin_service.model.Artifact;
import com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo.artifact.IArtifactRepository;
import com.dev_crazy.internal_distribution_app.admin_service.service.application.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArtifactService implements IArtifactService {
    @Autowired
    private IArtifactRepository artifactRepository;

    @Autowired
    private ApplicationService applicationService;


    @Override
    public Artifact findByCode(String resourceApplicationCode, String artifactCode) {
        Optional<Artifact> artifact = artifactRepository.findByCode(resourceApplicationCode, artifactCode);
        if (artifact.isPresent()) {
            return artifact.get();
        }else{
            throw new ArtifactNotFoundException();
        }
    }

    @Override
    public List<Artifact> findAll(String resourceApplicationCode, Map<String, Object> filters) {
        return artifactRepository.findAll(resourceApplicationCode, filters);
    }

    @Override
    public Artifact findLatest(String resourceApplicationCode) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("enabled", true);

        Optional<Artifact> artifact = artifactRepository.findLatest(resourceApplicationCode, filters);
        if (artifact.isPresent()) {
            return artifact.get();
        }else{
            throw new ArtifactNotFoundException();
        }
    }


    @Override
    public Artifact create(Artifact artifact) {
        artifact.generateResourceApplicationCode();

        applicationService.findByCode(artifact.getApplicationCode());
        Optional<Artifact> optionalArtifact = artifactRepository.findLatest(artifact.getResourceApplicationCode(), null);

        if (optionalArtifact.isPresent()) {
            Artifact latestArtifact = optionalArtifact.get();
            int compare = artifact.compareTo(latestArtifact);
            if (compare <= 0) {
                throw new ArtifactVersionException();
            }
        }

        artifact.generateArtifactId();
        artifact.setEnabled(false);
        Date currentDate = new Date();
        artifact.setCreated(currentDate);
        artifact.setUpdated(currentDate);

        artifactRepository.save(artifact);
        return artifact;
    }
}
