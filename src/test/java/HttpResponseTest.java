import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {
    @Test
    void testInvalidStatusCode() {
        InputStream stream = new ByteArrayInputStream("HTTP/1.1 Test\r\n".getBytes());
        assertThrows(IllegalStateException.class, () -> new HttpResponse<String>(stream, HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void testInvalidHeader() {
        InputStream stream = new ByteArrayInputStream("HTTP/1.1 200 OK\r\nTest\r\n\r\n".getBytes());
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> new HttpResponse<String>(stream, HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void testInvalidFirstLine() {
        InputStream stream = new ByteArrayInputStream("Test\r\n\r\nHost: Test".getBytes());
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> new HttpResponse<String>(stream, HttpResponse.BodyHandlers.ofString()));
    }
}