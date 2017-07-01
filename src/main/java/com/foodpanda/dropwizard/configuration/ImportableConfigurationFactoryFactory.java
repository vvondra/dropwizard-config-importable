package com.foodpanda.dropwizard.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.Validator;

import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;

@SuppressWarnings("unused")
public class ImportableConfigurationFactoryFactory<T> extends DefaultConfigurationFactoryFactory<T> {
    @Override
    public ConfigurationFactory<T> create(
        final Class<T> klass,
        final Validator validator,
        final ObjectMapper objectMapper,
        final String propertyPrefix
    ) {
        return new ImportableConfigurationFactory<T>(
            klass,
            validator,
            configureObjectMapper(objectMapper.copy()),
            propertyPrefix
        );
    }
}
