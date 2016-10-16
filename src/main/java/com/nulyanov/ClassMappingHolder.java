package com.nulyanov;


public interface ClassMappingHolder {
    /**
     * Checks mapping for current field name
     * @param property field name found in source
     * @param targetClass target object class, which contains current field
     * @return mapped field name in class
     */
    String getMappedName(String property, Class targetClass);
}
