package com.dev_crazy.internal_distribution_app.admin_service.service.application;

import com.dev_crazy.internal_distribution_app.admin_service.exception.application.ApplicationAlreadyExistsException;
import com.dev_crazy.internal_distribution_app.admin_service.exception.application.ApplicationNotFoundException;
import com.dev_crazy.internal_distribution_app.admin_service.model.*;
import com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo.application.IApplicationResourceRepository;
import com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo.application.IApplicationRepository;
import com.dev_crazy.internal_distribution_app.admin_service.service.keycloak.KeycloakAdminService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationService implements IApplicationService{

    @Autowired
    private IApplicationRepository applicationRepository;

    @Autowired
    private IApplicationResourceRepository applicationResourceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private KeycloakAdminService keycloakAdminService;

    @Override
    public Application findByCode(String applicationCode) {
        Optional<Application> optionalApplication = applicationRepository.findByCode(applicationCode);
        if (optionalApplication.isEmpty()) {
            throw new ApplicationNotFoundException();
        }
        return optionalApplication.get();
    }

    @Override
    public List<Application> findAll(Map<String, Object> filters) {
        return applicationRepository.findAll(filters);
    }

    @Override
    public ApplicationDetail getApplicationDetail(String applicationCode) {
        Application application = this.findByCode(applicationCode);
        List<ApplicationResource> applicationResources =  applicationRepository.getApplicationResources(applicationCode);

        if(applicationResources.isEmpty()){
            throw new ApplicationNotFoundException();
        }

        Set<Branch> branches = applicationResources.stream().map(ApplicationResource::getBranch).collect(Collectors.toSet());
        Set<Platform> platforms = applicationResources.stream().map(ApplicationResource::getPlatform).collect(Collectors.toSet());

        ApplicationDetail applicationDetail = modelMapper.map(application, ApplicationDetail.class);
        applicationDetail.setBranches(branches.stream().toList());
        applicationDetail.setPlatforms(platforms.stream().toList());

        return applicationDetail;
    }

    @Override
    public Application create(Application application) {
        Optional<Application> optionalApplication = applicationRepository.findByCode(application.getApplicationCode());
        if (optionalApplication.isPresent()) {
            throw new ApplicationAlreadyExistsException();
        }

        String applicationCode = application.getApplicationCode().toLowerCase();

        String resourceUriApplication = String.format("/application/%s/detail", applicationCode);
        String resourceUriAndroidQa = String.format("/application/%s.%s.%s/artifact/latest", applicationCode, Platform.ANDROID, Branch.QA);
        String resourceUriIosQa = String.format("/application/%s.%s.%s/artifact/latest", applicationCode, Platform.IOS, Branch.QA);
        String resourceUriAndroidPrd = String.format("/application/%s.%s.%s/artifact/latest", applicationCode, Platform.ANDROID, Branch.PROD);
        String resourceUriIosPrd = String.format("/application/%s.%s.%s/artifact/latest", applicationCode, Platform.IOS, Branch.PROD);

        String roleNameQa = String.format("user_%s_qa", applicationCode);
        String roleNamePrd = String.format("user_%s_prd", applicationCode);

        String resourceNameQa = String.format("application_%s_qa", applicationCode);
        String resourceNamePrd = String.format("application_%s_prd", applicationCode);

        String policyNameQa = String.format("user_%s_qa_policy", applicationCode);
        String policyNamePrd = String.format("user_%s_prd_policy", applicationCode);

        String roleIdQa = keycloakAdminService.createClientRole(roleNameQa);
        String roleIdPrd = keycloakAdminService.createClientRole(roleNamePrd);

        String resourceQaId = keycloakAdminService.createClientResource(resourceNameQa, List.of(resourceUriIosQa, resourceUriAndroidQa, resourceUriApplication));
        String resourcePrdId = keycloakAdminService.createClientResource(resourceNamePrd, List.of(resourceUriIosPrd, resourceUriAndroidPrd, resourceUriApplication));

        String policyIdQa = keycloakAdminService.createClientRolePolicy(policyNameQa, List.of(roleIdQa));
        String policyIdPrd = keycloakAdminService.createClientRolePolicy(policyNamePrd, List.of(roleIdPrd));

        String permissionNameQa = String.format("user_%s_qa_permission", applicationCode);
        String permissionNamePrd = String.format("user_%s_prd_permission", applicationCode);

        String readScope = keycloakAdminService.getScopeId("read");

        keycloakAdminService.createClientScopePermission(permissionNameQa,
                List.of(resourceQaId), List.of(policyIdQa), List.of(readScope));

        keycloakAdminService.createClientScopePermission(permissionNamePrd,
                List.of(resourcePrdId), List.of(policyIdPrd), List.of(readScope));

        Date currentDate = new Date();

        application.setCreated(currentDate);
        application.setUpdated(currentDate);
        application.setEnabled(true);

        applicationResourceRepository.save(new ApplicationResource(applicationCode, Branch.QA, Platform.ANDROID));
        applicationResourceRepository.save(new ApplicationResource(applicationCode, Branch.QA, Platform.IOS));
        applicationResourceRepository.save(new ApplicationResource(applicationCode, Branch.PROD, Platform.ANDROID));
        applicationResourceRepository.save(new ApplicationResource(applicationCode, Branch.PROD, Platform.IOS));

        return applicationRepository.save(application);
    }

    @Override
    public Application update(String applicationCode, Application application) {
        Application applicationCreated = this.findByCode(applicationCode);
        modelMapper.map(application, applicationCreated);
        applicationCreated.setUpdated(new Date());
        return applicationRepository.save(applicationCreated);
    }
}
