import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {
    @Test
    void testBuilderNotReturningNull() {
        HttpRequest request = HttpRequest.newBuilder().build();
        assertNotNull(request);
    }

    @Test
    void testBuilderMethodSet() {
        HttpRequest request = HttpRequest.newBuilder().GET().build();
        assertEquals("GET", request.method);
    }

    @Test
    void testBuilderMethodNotSet() {
        HttpRequest request = HttpRequest.newBuilder().build();
        assertNull(request.method);
    }

    @Test
    void testBuilderUriSet() throws URISyntaxException, MalformedURLException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).build();
        assertEquals(new URI("https://postman-echo.com/get").toURL(), request.url);
    }

    @Test
    void testBuilderUriNotSet() {
        HttpRequest request = HttpRequest.newBuilder().build();
        assertNull(request.url);
    }

    @Test
    void testBuilderHeaderSet() {
        HttpRequest request = HttpRequest.newBuilder().header("Host", "postman-echo.com").build();
        assertEquals("postman-echo.com", request.headers.get("Host"));
    }

    @Test
    void testBuilderHeaderNotSet() {
        HttpRequest request = HttpRequest.newBuilder().build();
        assertEquals(0, request.headers.size());
    }

    @Test
    void testBuilderBodySet() {
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString("Hello")).build();
        byte[] bytes = "Hello".getBytes();
        assertArrayEquals(bytes, request.body);
    }

    @Test
    void testBuilderBodyNotSet() {
        HttpRequest request = HttpRequest.newBuilder().build();
        assertNull(request.body);
    }

    @Test
    void testContentLengthString() {
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString("Hello")).build();
        assertEquals("Hello".getBytes().length, Integer.parseInt(request.headers.get("Content-Length")));
    }

    @Test
    void testContentLengthByteArray() {
        byte[] bytes = "Hello".getBytes();
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofByteArray(bytes)).build();
        assertEquals(bytes.length, Integer.parseInt(request.headers.get("Content-Length")));
    }
}