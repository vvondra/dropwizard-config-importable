package com.foodpanda.dropwizard.configuration;

import com.google.common.collect.ImmutableSet;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.error.Mark;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.dropwizard.configuration.ConfigurationException;


/**
 * This class is a shortened version of the original Dropwizard exception
 *
 * The original implementation was unfortunately package-private
 *
 * A {@link ConfigurationException} for errors parsing a configuration file.
 */
class ConfigurationParsingException extends ConfigurationException {

    static class Builder {
        private String summary;
        private String detail = "";
        private List<JsonMappingException.Reference> fieldPath = Collections.emptyList();
        private int line = -1;
        private int column = -1;
        private Exception cause = null;

        Builder(String summary) {
            this.summary = summary;
        }

        /**
         * Returns a brief message summarizing the error.
         *
         * @return a brief message summarizing the error.
         */
        public String getSummary() {
            return summary.trim();
        }

        /**
         * Returns a detailed description of the error.
         *
         * @return a detailed description of the error or the empty String if there is none.
         */
        public String getDetail() {
            return detail.trim();
        }

        /**
         * Determines if a detailed description of the error has been set.
         *
         * @return true if there is a detailed description of the error; false if there is not.
         */
        public boolean hasDetail() {
            return detail != null && !detail.isEmpty();
        }

        /**
         * Returns the path to the problematic JSON field, if there is one.
         *
         * @return a {@link List} with each element in the path in order, beginning at the root; or
         *         an empty list if there is no JSON field in the context of this error.
         */
        public List<JsonMappingException.Reference> getFieldPath() {
            return fieldPath;
        }

        /**
         * Determines if the path to a JSON field has been set.
         *
         * @return true if the path to a JSON field has been set for the error; false if no path has
         *         yet been set.
         */
        public boolean hasFieldPath() {
            return fieldPath != null && !fieldPath.isEmpty();
        }

        /**
         * Returns the line number of the source of the problem.
         * <p/>
         * Note: the line number is indexed from zero.
         *
         * @return the line number of the source of the problem, or -1 if unknown.
         */
        public int getLine() {
            return line;
        }

        /**
         * Returns the column number of the source of the problem.
         * <p/>
         * Note: the column number is indexed from zero.
         *
         * @return the column number of the source of the problem, or -1 if unknown.
         */
        public int getColumn() {
            return column;
        }

        /**
         * Determines if a location (line and column numbers) have been set.
         *
         * @return true if both a line and column number has been set; false if only one or neither
         *         have been set.
         */
        public boolean hasLocation() {
            return line > -1 && column > -1;
        }

        /**
         * Returns the {@link Exception} that encapsulates the problem itself.
         *
         * @return an Exception representing the cause of the problem, or null if there is none.
         */
        public Exception getCause() {
            return cause;
        }

        /**
         * Determines whether a cause has been set.
         *
         * @return true if there is a cause; false if there is none.
         */
        public boolean hasCause() {
            return cause != null;
        }

       ConfigurationParsingException.Builder setCause(Exception cause) {
            this.cause = cause;
            return this;
        }

       ConfigurationParsingException.Builder setDetail(String detail) {
            this.detail = detail;
            return this;
        }

       ConfigurationParsingException.Builder setLocation(Mark mark) {
            return mark == null
                ? this
                : setLocation(mark.getLine(), mark.getColumn());
        }

       ConfigurationParsingException.Builder setLocation(int line, int column) {
            this.line = line;
            this.column = column;
            return this;
        }

       ConfigurationParsingException build(String path) {
            final StringBuilder sb = new StringBuilder(getSummary());
            if (hasFieldPath()) {
                sb.append(" at: ").append(buildPath(getFieldPath()));
            } else if (hasLocation()) {
                sb.append(" at line: ").append(getLine() + 1)
                    .append(", column: ").append(getColumn() + 1);
            }

            if (hasDetail()) {
                sb.append("; ").append(getDetail());
            }

            return hasCause()
                ? new ConfigurationParsingException(path, sb.toString(), getCause())
                : new ConfigurationParsingException(path, sb.toString());
        }

        private String buildPath(Iterable<JsonMappingException.Reference> path) {
            final StringBuilder sb = new StringBuilder();
            if (path != null) {
                final Iterator<JsonMappingException.Reference> it = path.iterator();
                while (it.hasNext()) {
                    final JsonMappingException.Reference reference = it.next();
                    final String name = reference.getFieldName();

                    // append either the field name or list index
                    if (name == null) {
                        sb.append('[').append(reference.getIndex()).append(']');
                    } else {
                        sb.append(name);
                    }

                    if (it.hasNext()) {
                        sb.append('.');
                    }
                }
            }
            return sb.toString();
        }

    }

    /**
     * Create a mutable {@link ConfigurationParsingException.Builder} to incrementally build a
     * {@link ConfigurationParsingException}.
     *
     * @param brief the brief summary of the error.
     *
     * @return a mutable builder to incrementally build a {@link ConfigurationParsingException}.
     */
    static ConfigurationParsingException.Builder builder(String brief) {
        return new ConfigurationParsingException.Builder(brief);
    }

    /**
     * Creates a new ConfigurationParsingException for the given path with the given error.
     *
     * @param path   the bad configuration path
     * @param msg    the full error message
     */
    private ConfigurationParsingException(String path, String msg) {
        super(path, ImmutableSet.of(msg));
    }

    /**
     * Creates a new ConfigurationParsingException for the given path with the given error.
     *
     * @param path   the bad configuration path
     * @param msg    the full error message
     * @param cause  the cause of the parsing error.
     */
    private ConfigurationParsingException(String path, String msg, Throwable cause) {
        super(path, ImmutableSet.of(msg), cause);
    }

}
