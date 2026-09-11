package com.dev_crazy.internal_distribution_app.admin_service.service.artifact;

import com.dev_crazy.internal_distribution_app.admin_service.exception.BaseServiceException;
import com.dev_crazy.internal_distribution_app.admin_service.exception.artifact.ArtifactNotFoundException;
import com.dev_crazy.internal_distribution_app.admin_service.exception.artifact.ArtifactVersionException;
import com.dev_crazy.internal_distribution_app.admin_service.model.Artifact;
import com.dev_crazy.internal_distribution_app.admin_service.model.BinaryDetail;
import com.dev_crazy.internal_distribution_app.admin_service.model.Metadata;
import com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo.artifact.IArtifactBinaryRepository;
import com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo.artifact.IArtifactRepository;
import com.dev_crazy.internal_distribution_app.admin_service.service.application.ApplicationService;
import com.dev_crazy.internal_distribution_app.admin_service.service.storage.IStorageService;
import com.dev_crazy.internal_distribution_app.admin_service.util.ExtractMetadataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.*;

@Service
public class ArtifactService implements IArtifactService {
    @Autowired
    private IArtifactRepository artifactRepository;

    @Autowired
    private IArtifactBinaryRepository artifactBinaryRepository;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private IStorageService storageService;


    @Override
    public Artifact findByCode(String resourceApplicationCode, String artifactCode) {
        Optional<Artifact> artifactOptional = artifactRepository.findByCode(resourceApplicationCode, artifactCode);
        Optional<BinaryDetail> binaryDetailOptional = artifactBinaryRepository.findById(artifactCode);

        if (artifactOptional.isPresent()) {
            Artifact artifact1 = artifactOptional.get();

            if(binaryDetailOptional.isPresent()) {
                BinaryDetail binaryDetail = binaryDetailOptional.get();

                String url = storageService.getObject(binaryDetail.getKeypath());
                binaryDetail.setUrl(url);

                artifact1.setBinaryDetail(binaryDetail);
            }
            return artifact1;
        }else{
            throw new ArtifactNotFoundException();
        }

    }

    @Override
    public BinaryDetail getBinaryDetail(String artifactCode) {
        Optional<BinaryDetail> binaryDetail = artifactBinaryRepository.findById(artifactCode);
        if (binaryDetail.isPresent()) {
            return binaryDetail.get();
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

    @Override
    public Artifact saveBinary(BinaryDetail binaryDetail, InputStream inputStream, String resourceApplicationCode, String artifactCode) {

        Metadata metadata = ExtractMetadataUtil.extractAndroidMetadata(inputStream);
        try {
            inputStream.reset();
        }catch (Exception e){
            throw new BaseServiceException(e.getMessage(), 500, e);
        }

        Artifact artifact1 = this.findByCode(resourceApplicationCode, artifactCode);

        if(!artifact1.getVersion().equalsIgnoreCase(metadata.getVersionName()) ||
            !artifact1.getApplicationCode().equalsIgnoreCase(metadata.getPackageName())){
            throw new BaseServiceException("Paquete o versión del ejecutable no coinciden", 400, null);
        }

        String extension = binaryDetail.getFilename().substring(binaryDetail.getFilename().lastIndexOf(".") + 1);
        String filename = String.format("%s.%s.%s", resourceApplicationCode, artifact1.getVersion(), extension);

        binaryDetail.setFilename(filename);
        binaryDetail.setKeypath(String.format("%s/%s/%s", resourceApplicationCode.replace(".", "_"), artifact1.getVersion().replace(".", "_"), filename));
        artifact1.setEnabled(true);

        storageService.saveObject(binaryDetail, inputStream);

        artifactBinaryRepository.save(binaryDetail, artifactCode);
        artifact1 = artifactRepository.save(artifact1);

        artifact1.setBinaryDetail(binaryDetail);
        return artifact1;
    }
}
