package com.foodpanda.dropwizard.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.validation.Validator;

import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;

import static java.util.Objects.requireNonNull;

/**
 * Provides support for importing other YAML files in a YAML configuration files.
 *
 * This way, one can have a base file which can have configuration properties
 * added or overridden by a wrapping configuration file.
 *
 * Other original behaviour of the default Dropwizard configuration factory is preserved.
 */
class ImportableConfigurationFactory<T> extends YamlConfigurationFactory<T> {

    private final ObjectMapper objectMapper;

    /*
     * There is attempt to avoid name-clash if Dropwizard makes
     * the parent property protected or public
     */
    private final YAMLFactory overriddenYamlFactory;

    private static final String IMPORT_KEY = "imports";

    /**
     * Creates a new configuration factory for the given class.
     *
     * @param klass          the configuration class
     * @param validator      the validator to use
     * @param objectMapper   the Jackson {@link ObjectMapper} to use
     * @param propertyPrefix the system property name prefix used by overrides
     */
    ImportableConfigurationFactory(
        final Class<T> klass,
        final Validator validator,
        final ObjectMapper objectMapper,
        final String propertyPrefix
    ) {
        super(klass, validator, objectMapper, propertyPrefix);
        this.objectMapper = objectMapper;
        this.overriddenYamlFactory = new YAMLFactory();
    }

    /**
     * Loads, parses, binds, and validates a configuration object.
     *
     * @param provider the provider to to use for reading configuration files
     * @param path     the path of the configuration file
     * @return a validated configuration object
     * @throws IOException            if there is an error reading the file
     * @throws ConfigurationException if there is an error parsing or validating the file
     */
    public T build(ConfigurationSourceProvider provider, String path)
        throws IOException, ConfigurationException
    {
        return build(loadConfiguration(provider, path), path);
    }

    private JsonNode loadConfiguration(ConfigurationSourceProvider provider, String path)
        throws IOException, ConfigurationException
    {
        try (InputStream input = provider.open(requireNonNull(path))) {
            JsonNode node = objectMapper.readTree(overriddenYamlFactory.createParser(input));

            if (node == null) {
                throw new ConfigurationParsingException(
                    path,
                    "Configuration at " + path + " must not be empty"
                );
            }

            // Look for the imports array
            if (node.has(IMPORT_KEY) && node.get(IMPORT_KEY).isArray()) {
                for (JsonNode configImport : node.get(IMPORT_KEY)) {
                    // The element must be a string path
                    if (configImport.isTextual()) {
                        // Recurse and load nested configuration and merge the current file
                        // into the imported one
                        JsonNode imported = loadConfiguration(
                            provider,
                            new File(new File(path).getParent(), configImport.asText()).toString()
                        );

                        node = JsonUtil.merge(imported, node);
                    }
                }

                // Remove the import key itself from the file
                // There is no use for it to map the configuration
                node = ((ObjectNode) node).without(IMPORT_KEY);
            }


            return node;
        } catch (YAMLException e) {
//            final ConfigurationParsingException.Builder builder = ConfigurationParsingException
//                .builder("Malformed YAML")
//                .setCause(e)
//                .setDetail(e.getMessage());
//
//            if (e instanceof MarkedYAMLException) {
//                builder.setLocation(((MarkedYAMLException) e).getProblemMark());
//            }

            throw new ConfigurationParsingException(
                path,
                "Malformed YAML",
                e
            );
        }
    }
}
