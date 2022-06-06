package io.cloudflight.structurizr.plantuml;

import javax.annotation.Nullable;
import java.util.Map;

public class MapUtils {

    private MapUtils() {
    }

    /**
     * Transforms the given map into a comma-separated String of key-value pairs, i.e.: <code>key1=value1, key2=value2</code>.
     * The order of the entries is given by {@link Map#entrySet()}, therefore use a {@link java.util.LinkedHashMap} if
     * you want to influence the order of the entries.
     *
     * @param map a map of values
     * @return <code>null</code>, if the map is null. an empty string of the map is empty,
     * else a comma-separated string of key/value-pairs
     */
    @Nullable
    public static String mapToString(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        if (map.isEmpty()) {
            return "";
        }

        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            buf.append(String.format("%s=%s, ", entry.getKey(), entry.getValue()));
        }

        String tagsAsString = buf.toString();
        return tagsAsString.substring(0, tagsAsString.length() - 2);
    }
}
