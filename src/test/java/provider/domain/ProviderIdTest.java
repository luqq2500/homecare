package provider.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProviderIdTest {

    @Test
    void generateProducesUniqueIds() {
        ProviderId a = ProviderId.generate();
        ProviderId b = ProviderId.generate();
        assertNotEquals(a, b);
        assertNotNull(a.value());
    }

    @Test
    void ofAndEqualsHashCodeToString() {
        ProviderId a = ProviderId.of("prov-1");
        ProviderId b = ProviderId.of("prov-1");
        ProviderId c = ProviderId.of("prov-2");
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals("prov-1", a.toString());
        assertEquals("prov-1", a.value());
    }
}
