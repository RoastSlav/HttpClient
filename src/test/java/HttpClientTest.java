import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientTest {
    @Test
    void testBuilderNotReturningNull() {
        HttpClient client = HttpClient.newBuilder().build();
        assertNotNull(client);
    }

    @Test
    void testBuilderFollowRedirectsSet() {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        assertEquals(HttpClient.Redirect.ALWAYS, client.followRedirects);
    }

    @Test
    void testBuilderFollowRedirectsNotSet() {
        HttpClient client = HttpClient.newBuilder().build();
        assertEquals(HttpClient.Redirect.NORMAL, client.followRedirects);
    }

    @Test
    void testGETSend() throws URISyntaxException, MalformedURLException {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(new URI("https://postman-echo.com/get")).build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertNotNull(response);
    }

    @Test
    void testGETSendWithSavingToFile() {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("https://postman-echo.com/get")).build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<File> response = client.send(request, HttpResponse.BodyHandlers.ofFile(Path.of("src/test/resources/GET.txt")));
        assertAll(
                () -> assertNotNull(response),
                () -> assertTrue(response.body().exists()),
                () -> assertEquals(response.headers().firstValue("Content-Length").get(), String.valueOf(response.body().length()))
        );
    }

    @Test
    void testGETSendWithRedirect() throws URISyntaxException, MalformedURLException {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(new URI("https://postman-echo.com/get")).build();
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertNotNull(response);
    }

    @Test
    void testPOSTSend() throws URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post")).POST(HttpRequest.BodyPublishers.ofString("Hello")).build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(200, response.statusCode),
                () -> assertTrue(response.body().contains("\"data\":\"Hello\""))
        );
    }

    @Test
    void testDefaultRequestHeaders() throws URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(new URI("https://postman-echo.com/get")).build();
        HttpClient client = HttpClient.newBuilder().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        assertAll(
                () -> assertEquals("postman-echo.com", request.headers.get("Host")),
                () -> assertEquals("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8", request.headers.get("Accept")),
                () -> assertEquals("gzip, deflate, br", request.headers.get("Accept-Encoding")),
                () -> assertEquals("en-US,en;q=0.5", request.headers.get("Accept-Language")),
                () -> assertEquals("close", request.headers.get("Connection")),
                () -> assertEquals("1", request.headers.get("Upgrade-Insecure-Requests"))
        );
    }

    @Test
    void testReplacingDefaultRequestHeaders() throws URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(new URI("https://postman-echo.com/get")).header("Connection", "Keep-Alive").build();
        HttpClient client = HttpClient.newBuilder().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Keep-Alive", request.headers.get("Connection"));
    }
}