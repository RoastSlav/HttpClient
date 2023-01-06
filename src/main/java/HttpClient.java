import java.io.*;
import java.net.Socket;

public class HttpClient {
    private static final int DEFAULT_HTTP_PORT = 80;

    enum Redirect {
        ALWAYS, NEVER, NORMAL
    }

    static class Builder {
        HttpClient client = new HttpClient();

        Builder followRedirects(HttpClient.Redirect policy) {
            client.followRedirects = policy;
            return this;
        }

        HttpClient build() {
            return client;
        }
    }

    public HttpClient.Redirect followRedirects = HttpClient.Redirect.NORMAL;

    public static HttpClient.Builder newBuilder() {
        return new Builder();
    }

    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandlers<T> bodyHandler) {
        int portToUse = DEFAULT_HTTP_PORT;
        if (request.url.getPort() != -1) {
            portToUse = request.url.getPort();
        }

        fillHeaders(request);

        try (Socket socket = new Socket(request.url.getHost(), portToUse);
             OutputStream outputStream = socket.getOutputStream()) {

            PrintWriter output = new PrintWriter(outputStream);
            String requestString = request.method + " " + request.url.getPath() + " HTTP/1.1";
            output.println(requestString);
            for (String key : request.headers.keySet()) {
                output.println(key + ": " + request.headers.get(key));
            }
            output.println();
            output.flush();

            if (request.body != null) {
                outputStream.write(request.body);
            }

            return new HttpResponse<T>(socket.getInputStream(), bodyHandler);
        } catch (IOException e) {
            //TODO what to do here?
            e.printStackTrace();
            return null;
        }
    }

    private static void fillHeaders(HttpRequest request) {
        request.headers.putIfAbsent("Host", request.url.getHost());
        request.headers.putIfAbsent("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
        request.headers.putIfAbsent("Accept-Language", "en-US,en;q=0.5");
        request.headers.putIfAbsent("Accept-Encoding", "gzip, deflate, br");
        request.headers.putIfAbsent("Connection", "close");
        request.headers.putIfAbsent("Upgrade-Insecure-Requests", "1");
    }

    public static HttpClient newHttpClient() {
        return newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    }
}
