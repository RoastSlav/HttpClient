import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpHeadersTest {
    @Test
    void testAddingHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "postman-echo.com");
        assertEquals("postman-echo.com", headers.firstValue("Host").get());
    }

    @Test
    void testAddingMultipleHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "postman-echo.com");
        headers.add("Host", "postman-echo.com");
        assertEquals(1, headers.allValues("Host").size());
    }

    @Test
    void testAddingMultipleHeadersWithDifferentKeys() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "postman-echo.com");
        headers.add("User-Agent", "Mozilla/5.0");
        assertEquals(1, headers.allValues("Host").size());
        assertEquals(1, headers.allValues("User-Agent").size());
    }

    @Test
    void testHeadersFromMap() {
        Map<String, String> map = Map.of("Host", "postman-echo.com");
        HttpHeaders headers = HttpHeaders.of(map);
        assertEquals("postman-echo.com", headers.firstValue("Host").get());
    }

    @Test
    void testMapFromHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "postman-echo.com");
        Map<String, String> map = headers.map();
        assertEquals("postman-echo.com", map.get("Host"));
    }

    @Test
    void testGetAllValues() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "postman-echo.com");
        headers.add("User-Agent", "Mozilla/5.0");
        assertEquals("postman-echo.com", headers.allValues("Host").get(0));
    }
}