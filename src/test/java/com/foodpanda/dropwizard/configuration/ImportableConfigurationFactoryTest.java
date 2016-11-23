package com.foodpanda.dropwizard.configuration;

import com.google.common.io.Resources;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.validation.BaseValidator;

import static org.junit.Assert.assertEquals;

public class ImportableConfigurationFactoryTest {

    @SuppressWarnings("UnusedDeclaration")
    public static class Example {
        @NotNull
        private String name;

        @JsonProperty
        private Map<String, String> properties = new LinkedHashMap<>();

        public String getName() {
            return name;
        }

        public Map<String, String> getProperties() {
            return properties;
        }
    }


    private final Validator validator = BaseValidator.newValidator();
    private final ConfigurationFactory<Example> factory = new ImportableConfigurationFactory<>(
        Example.class,
        validator,
        Jackson.newObjectMapper(),
        "fp"
    );
    private File malformedFile;
    private File emptyFile;
    private File invalidFile;
    private File validFile;
    private File importingFile;

    private static File resourceFileName(String resourceName) throws URISyntaxException {
        return new File(Resources.getResource(resourceName).toURI());
    }

    @After
    public void resetConfigOverrides() {
        for (Enumeration<?> props = System.getProperties().propertyNames(); props.hasMoreElements();) {
            String keyString = (String) props.nextElement();
            if (keyString.startsWith("dw.")) {
                System.clearProperty(keyString);
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        this.validFile = resourceFileName("factory-test-valid.yml");
        this.importingFile = resourceFileName("factory-test-importing.yml");
    }

    @Test
    public void importConfigurationFile() throws Exception {
        final Example example = factory.build(importingFile);

        assertEquals("Importing Foodpanda", example.getName());
    }

}
