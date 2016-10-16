package com.nulyanov;

import com.nulyanov.model.Address;
import com.nulyanov.model.Person;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class JsonToTargetClassParserTest {
    private final String PATH_TO_ADDRESS_JSON = "src/test/resources/address.json";
    private final String PATH_TO_PERSON_JSON = "src/test/resources/person.json";
    private final String PERSON_ADDRESS = "471 Harrison Avenue, Tedrow, Delaware";
    private final JsonToTargetClassParser jsonToTargetClassParser = new JsonToTargetClassParserImpl();
    private static Map<String, String> personJsonPathToClassProperties = new HashMap<>();
    private static Map<String, String> addressJsonPathToClassProperties = new HashMap<>();
    private static String[] phones = {"+1 (984) 400-3912","+1 (878) 469-3989","+1 (945) 457-3362"};

    @BeforeClass
    public static void setUpPersonJsonPathToClassProperties(){
        personJsonPathToClassProperties.put("firstName", "name");
        personJsonPathToClassProperties.put("lastName", "surname");
        personJsonPathToClassProperties.put("age", "age");
        personJsonPathToClassProperties.put("direction", "address");
        personJsonPathToClassProperties.put("phoneNumbers", "phones");

        addressJsonPathToClassProperties.put("address", "fullAddress");
        addressJsonPathToClassProperties.put("zip", "zipCode");
    }

    @Test
    public void whenJsonWithAddressThenReturnAddressInstance(){
        Object resultObject =
                jsonToTargetClassParser.parseObjectFromJson(
                        Address.class,
                        addressJsonPathToClassProperties,
                        PATH_TO_ADDRESS_JSON);

        assertEquals("Result object is not an instance of address", Address.class, resultObject.getClass());
    }

    @Test
    public void whenJsonWithPersonThenReturnPersonWithRightAddress(){
        Object resultObject =
                jsonToTargetClassParser.parseObjectFromJson(
                        Person.class,
                        personJsonPathToClassProperties,
                        PATH_TO_PERSON_JSON);

        Person person = (Person) resultObject;
        Address address = person.getAddress();

        assertEquals("Result person has invalid address", PERSON_ADDRESS, address.getFullAddress());
    }

    @Test
    public void whenJsonWithPersonThenReturnPersonWithAllPhones(){
        Object resultObject =
                jsonToTargetClassParser.parseObjectFromJson(
                        Person.class,
                        personJsonPathToClassProperties,
                        PATH_TO_PERSON_JSON);

        Person person = (Person) resultObject;

        assertArrayEquals("Result person has invalid phones", phones, person.getPhones());
    }
}