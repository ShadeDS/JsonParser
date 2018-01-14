package com.nulyanov;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonToTargetClassParserImpl implements JsonToTargetClassParser {

    private Class targetClass;
    private Map<String, String> jsonPathToClassProperties;

    @Override
    public Object parseObjectFromJson(Class objectClass, Map<String, String> jsonPathToClassProperties, String pathToJson) {
        this.targetClass = objectClass;
        this.jsonPathToClassProperties = jsonPathToClassProperties;

        try (JsonParser jsonParser = new JsonFactory().createParser(new File(pathToJson))){
            return getObject(jsonParser, objectClass);
        }
        catch (IOException | InstantiationException | IllegalAccessException | NoSuchFieldException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parses json to get an object
     * @param jsonParser
     * @param objectClass class of target object
     * @return built object of specified class
     * @throws IOException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    private Object getObject(JsonParser jsonParser, Class objectClass) throws IOException, IllegalAccessException, NoSuchFieldException, InstantiationException, ClassNotFoundException {
        if (objectClass.isPrimitive() || objectClass == String.class){
            return getPrimitive(jsonParser);
        }

        Object targetObject = objectClass.newInstance();
        List<Field> fields = Arrays.asList(objectClass.getDeclaredFields());
        while(jsonParser.nextToken() != JsonToken.END_OBJECT){
            String currentName = jsonParser.getCurrentName();
            String mappedName = objectClass == this.targetClass ? getMappedName(currentName) : currentName;

            if (stringIsFieldName(fields, mappedName)){
                jsonParser.nextToken();
                Field field = objectClass.getDeclaredField(mappedName);
                Object value = getValue(jsonParser, field.getType());

                field.setAccessible(true);
                field.set(targetObject, value);
            }
        }
        return targetObject;
    }

    /**
     * Determines the type of value (object, array, primitive) and gets the right value
     * @param jsonParser
     * @param fieldClass type of field for value
     * @return value for current field
     * @throws IOException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     */
    private Object getValue(JsonParser jsonParser, Class fieldClass) throws IOException, IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchFieldException {
        JsonToken currentToken = jsonParser.getCurrentToken();

        if(JsonToken.START_ARRAY == currentToken){
            return getArray(jsonParser, fieldClass.getComponentType());
        }
        else if (JsonToken.START_OBJECT == currentToken){
            return getObject(jsonParser, fieldClass);
        }
        else {
            return getPrimitive(jsonParser);
        }
    }

    /**
     * Checks if current name from source is a field name of current object
     * @param fields list of object's fields
     * @param currentName name from source
     * @return true if current name is a field, false otherwise
     */
    private boolean stringIsFieldName(List<Field> fields, String currentName) {
        for (Field field : fields){
            if (field.getName().equals(currentName)){
                return true;
            }
        }
        return false;
    }

    /**
     * Parses json to get an array
     * @param jsonParser
     * @param componentType type of values in array
     * @return array with specified component type
     * @throws IOException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     */
    private <T> Object getArray(JsonParser jsonParser, Class componentType) throws IOException, IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchFieldException {
        List<Object> list = new LinkedList<>();

        while(jsonParser.nextToken() != JsonToken.END_ARRAY){
            Object value = getValue(jsonParser, componentType);
            list.add(value);
        }

        T[] target = (T[]) Array.newInstance(componentType, list.size());
        return list.toArray(target);
    }

    /**
     * Parses json to get a primitive
     * @param jsonParser
     * @return value of primitive field
     * @throws IOException
     * @throws IllegalAccessException
     */
    @Nullable
    private Object getPrimitive(JsonParser jsonParser) throws IOException, IllegalAccessException {
        JsonToken currentToken = jsonParser.getCurrentToken();

        if (JsonToken.VALUE_STRING == currentToken){
            return jsonParser.getText();
        }
        else if (JsonToken.VALUE_NUMBER_INT == currentToken){
            return jsonParser.getIntValue();
        }
        else if (JsonToken.VALUE_NUMBER_FLOAT == currentToken){
            return jsonParser.getFloatValue();
        }
        else if (JsonToken.VALUE_TRUE == currentToken ||  JsonToken.VALUE_FALSE == currentToken){
            return jsonParser.getBooleanValue();
        }
        return null;
    }

    /**
     * Converts original property name to mapped one
     * @param property name of property from json
     * @return mapped name of property or the same name if there is no mapping for this property
     */
    private String getMappedName(String property) {
        String mappedFieldName = jsonPathToClassProperties.get(property);
        if (mappedFieldName != null) {
            return mappedFieldName;
        }
        return property;
    }
}