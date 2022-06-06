package io.cloudflight.structurizr.plantuml;

import org.junit.jupiter.api.Test;

import static io.cloudflight.structurizr.plantuml.StringUtils.quote;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringUtilsTest {

    @Test
    void quoteNull() {
        assertNull(quote(null));
    }

    @Test
    void quoteEmptyString() {
        assertEquals("\"\"", quote(""));
    }

    @Test
    void quoteAnyString() {
        assertEquals("\"foo\"", quote("foo"));
    }
}
