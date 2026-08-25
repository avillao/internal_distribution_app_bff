package com.dev_crazy.internal_distribution_app.admin_service.service.application;

import com.dev_crazy.internal_distribution_app.admin_service.exception.application.ApplicationAlreadyExistsException;
import com.dev_crazy.internal_distribution_app.admin_service.exception.application.ApplicationNotFoundException;
import com.dev_crazy.internal_distribution_app.admin_service.model.Application;
import com.dev_crazy.internal_distribution_app.admin_service.repository.IApplicationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ApplicationService implements IApplicationService{

    @Autowired
    private IApplicationRepository applicationRepository;
    @Autowired
    private ModelMapper modelMapper;

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
    public Application create(Application application) {
        Optional<Application> optionalApplication = applicationRepository.findByCode(application.getApplicationCode());
        if (optionalApplication.isPresent()) {
            throw new ApplicationAlreadyExistsException();
        }

        application.setCreated(new Date());
        application.setUpdated(new Date());
        application.setEnabled(true);
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
