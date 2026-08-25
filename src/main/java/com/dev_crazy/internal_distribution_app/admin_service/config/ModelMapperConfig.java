package com.dev_crazy.internal_distribution_app.admin_service.config;

import com.dev_crazy.internal_distribution_app.admin_service.model.Branch;
import com.dev_crazy.internal_distribution_app.admin_service.model.Platform;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        Converter<String, Platform> platformConverter = new AbstractConverter<>() {
            @Override
            protected Platform convert(String source) {
                return source == null ? null : Platform.valueOfString(source);
            }
        };

        Converter<String, Branch> branchConverter = new AbstractConverter<>() {
            @Override
            protected Branch convert(String source) {
                return source == null ? null : Branch.valueOfString(source);
            }
        };

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.addConverter(platformConverter);
        modelMapper.addConverter(branchConverter);
        return modelMapper;
    }
}
