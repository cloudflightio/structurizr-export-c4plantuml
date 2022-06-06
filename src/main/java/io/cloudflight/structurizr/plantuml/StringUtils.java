package io.cloudflight.structurizr.plantuml;

public class StringUtils {

    private StringUtils() {
    }

    public static String quote(String value) {
        if (value == null) {
            return null;
        }
        return String.format("\"%s\"", value);
    }
}
