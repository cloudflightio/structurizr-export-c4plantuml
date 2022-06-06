package io.cloudflight.structurizr.plantuml;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.cloudflight.structurizr.plantuml.MapUtils.mapToString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapUtilsTest {

    @Test
    void mapNull() {
        assertNull(mapToString(null));
    }

    @Test
    void mapEmptyMap() {
        assertEquals("", mapToString(new HashMap<>()));
    }

    @Test
    void mapSingleValue() {
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        assertEquals("key=value", mapToString(map));
    }

    @Test
    void mapMultipleValues() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("key", "value");
        map.put("foo", "bar");
        assertEquals("key=value, foo=bar", mapToString(map));
    }

    @Test
    void mapMultipleValues_OtherOrder() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("foo", "bar");
        map.put("key", "value");
        assertEquals("foo=bar, key=value", mapToString(map));
    }
}
