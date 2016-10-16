package com.nulyanov;

import java.util.Map;


public class ClassMappingHolderImpl implements ClassMappingHolder {
    private Class targetClass;
    private Map<String, String> jsonPathToClassProperties;

    public ClassMappingHolderImpl(Class targetClass, Map<String, String> jsonPathToClassProperties) {
        this.targetClass = targetClass;
        this.jsonPathToClassProperties = jsonPathToClassProperties;
    }

    @Override
    public String getMappedName(String property, Class targetClass) {
        if (targetClass == this.targetClass){
            String mappedFieldName = jsonPathToClassProperties.get(property);
            if (mappedFieldName != null){
                return mappedFieldName;
            }
        }
        return property;
    }
}
