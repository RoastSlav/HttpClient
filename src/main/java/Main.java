import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws URISyntaxException {
        //Example 1
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/get"))
                .GET()
                .build();

        //Example 2
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/get"))
                .headers("key1", "value1", "key2", "value2")
                .GET()
                .build();

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/get"))
                .header("key1", "value1")
                .header("key2", "value2")
                .GET()
                .build();

        //Example 3
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/post"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpRequest request5 = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/post"))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString("Sample request body"))
                .build();

        byte[] sampleData = "Sample request body".getBytes();
        HttpRequest request6 = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/post"))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofByteArray(sampleData))
                .build();

        // optional
        byte[] sampleData2 = "Sample request body".getBytes();
        HttpRequest request7 = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/post"))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers
                        .ofInputStream(() -> new ByteArrayInputStream(sampleData)))
                .build();

        HttpRequest request8 = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/post"))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.fromFile(
                        Paths.get("src/test/resources/sample.txt")))
                .build();

        //Example 4
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // optional
        HttpResponse<String> response2 = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        //Example 5
        HttpResponse<String> response3 = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        HttpHeaders responseHeaders = response.headers();
        System.out.println(response.body());

        //Example 6
        HttpResponse<File> response4 = client.send(request,
                HttpResponse.BodyHandlers.ofFile(Path.of("testFile.txt")));
    }
}