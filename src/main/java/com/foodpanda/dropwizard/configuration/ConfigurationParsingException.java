package com.foodpanda.dropwizard.configuration;

import com.google.common.collect.ImmutableSet;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.error.Mark;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

import io.dropwizard.configuration.ConfigurationException;

/**
 * A {@link ConfigurationException} for errors parsing a configuration file.
 */
public class ConfigurationParsingException extends ConfigurationException {
    /**
     * Creates a new ConfigurationParsingException for the given path with the given error.
     *
     * @param path   the bad configuration path
     * @param msg    the full error message
     */
    ConfigurationParsingException(String path, String msg) {
        super(path, ImmutableSet.of(msg));
    }

    /**
     * Creates a new ConfigurationParsingException for the given path with the given error.
     *
     * @param path   the bad configuration path
     * @param msg    the full error message
     * @param cause  the cause of the parsing error.
     */
    ConfigurationParsingException(String path, String msg, Throwable cause) {
        super(path, ImmutableSet.of(msg), cause);
    }

}
