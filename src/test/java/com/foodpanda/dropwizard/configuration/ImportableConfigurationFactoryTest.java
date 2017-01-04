package com.foodpanda.dropwizard.configuration;

import com.google.common.io.Resources;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.validation.BaseValidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ImportableConfigurationFactoryTest {

    @SuppressWarnings("UnusedDeclaration")
    public static class SimpleExample {
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


    @After
    public void resetConfigOverrides() {
        for (Enumeration<?> props = System.getProperties().propertyNames(); props.hasMoreElements();) {
            String keyString = (String) props.nextElement();
            if (keyString.startsWith("dw.")) {
                System.clearProperty(keyString);
            }
        }
    }

    @Test
    public void importConfigurationFile() throws Exception {
        final ConfigurationFactory<SimpleExample> factory = createFactory(SimpleExample.class);

        final File validFile = resourceFileName("factory-test-valid.yml");
        final SimpleExample base = factory.build(validFile);
        assertTrue(Boolean.valueOf(base.getProperties().get("debug")));
        assertEquals("foo", base.getProperties().get("extra"));
        assertNull(base.getProperties().get("extended"));

        final File importingFile = resourceFileName("factory-test-importing.yml");
        final SimpleExample example = factory.build(importingFile);

        assertEquals("Importing Foodpanda", example.getName());
        assertFalse(Boolean.valueOf(example.getProperties().get("debug")));
        assertEquals("foo", example.getProperties().get("extra"));
        assertEquals("bar", example.getProperties().get("extended"));
    }

    private static File resourceFileName(String resourceName) throws URISyntaxException {
        return new File(Resources.getResource(resourceName).toURI());
    }

    private <T> ImportableConfigurationFactory<T> createFactory(Class<T> klass) {
        return new ImportableConfigurationFactory<>(
            klass,
            BaseValidator.newValidator(),
            Jackson.newObjectMapper(),
            "fp"
        );
    }
}
