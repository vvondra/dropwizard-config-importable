package com.foodpanda.dropwizard.configuration;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;

class JsonUtil {

    private JsonUtil() {
        // Static helper class
    }

    /**
     * Merges two JSON nodes
     *
     * The rule is simply that object properties with the same key get overridden
     * by the value in mergedNode, e.g. arrays are not merged but overwritten
     *
     * The semantics are similar to the JS <code>$.extends</code>
     *
     * @param extendedNode First node
     * @param mergedNode Node which properties are added to the second one
     * @return extendedNode with properties added from mergedNode
     */
    static JsonNode merge(JsonNode extendedNode, JsonNode mergedNode) {
        Iterator<String> fieldNames = mergedNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode jsonNode = extendedNode.get(fieldName);

            // if field exists and is an embedded object
            if (jsonNode != null && jsonNode.isObject()) {
                merge(jsonNode, mergedNode.get(fieldName));
            } else {
                if (extendedNode instanceof ObjectNode) {
                    // Overwrite field
                    JsonNode value = mergedNode.get(fieldName);
                    ((ObjectNode) extendedNode).replace(fieldName, value);
                }
            }

        }

        return extendedNode;
    }
}
