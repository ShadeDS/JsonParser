package com.nulyanov;

import java.util.Map;

public interface JsonToTargetClassParser {

    /**
     * Parse object from json to instance of specified class
     * @param objectClass class for target object
     * @param jsonPathToClassProperties jsonPath and field name of the data class mapping
     * @param pathToJson input file with object
     * @return object parsed from json
     */
    Object parseObjectFromJson(Class objectClass, Map<String, String> jsonPathToClassProperties, String pathToJson);
}
