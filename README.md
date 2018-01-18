# Json Parser

Class to parse json into java object with additional mapping for properties 

## Problem 

Write json parser with using `com.fasterxml.jackson.core.JsonParser`  
Input parameters:

* `Class` (java class with properties, default constructor, getters/setters)
* `Map` (where key is property name in json and value is property name in target class)
* `Json`

Result: `Builded object`

## Tests

Unit tests are located in `JsonToTargetClassParserTest.java`