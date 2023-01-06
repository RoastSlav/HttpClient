import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;

public class HttpResponse<T> {
    static class BodyHandlers<T> {
        T body;

        public static BodyHandlers<String> ofString() {
            BodyHandlers<String> stringBodyHandler = new BodyHandlers<>();
            stringBodyHandler.body = "";
            return stringBodyHandler;
        }

        public static BodyHandlers<File> ofFile(Path path) {
            BodyHandlers<File> fileBodyHandlers = new BodyHandlers<>();
            fileBodyHandlers.body = path.toFile();
            return fileBodyHandlers;
        }
    }

    private final T body;
    private final HashMap<String, String> headers = new HashMap<>();
    protected int statusCode;
    protected String protocol;

    protected HttpResponse(InputStream stream, BodyHandlers<?> handler) throws IOException {
        parseStatusAndHeaders(stream);
        body = parseBody(stream, handler);
    }

    private void parseStatusAndHeaders(InputStream stream) throws IOException {
        StringBuilder line = new StringBuilder();
        for (int readBytes; (readBytes = stream.read()) != -1; ) {
            line.append((char) readBytes);

            if (line.toString().endsWith("\r\n")) {
                String lineString = line.toString().trim();
                if (lineString.isEmpty()) {
                    break;
                }
                //TODO parse the first lineString independently
                if (lineString.startsWith("HTTP/")) {
                    String[] parts = lineString.split(" ");
                    protocol = parts[0];
                    try {
                        statusCode = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        throw new IllegalStateException("Invalid status code: " + parts[1]);
                    }
                } else {
                    String[] parts = lineString.split(": ");
                    headers.put(parts[0], parts[1]);
                }
                line.setLength(0);
            }
        }
    }

    private T parseBody(InputStream stream, BodyHandlers<?> handler) throws IOException {
        if (handler.body instanceof String) {
            StringBuilder sb = new StringBuilder();
            for (int readBytes; (readBytes = stream.read()) != -1; ) {
                sb.append((char) readBytes);
            }
            return (T) sb.toString();
        } else if (handler.body instanceof File file) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                stream.transferTo(fos);
            }
            return (T) file;
        }
        return null;
    }

    public HttpHeaders headers() {
        return HttpHeaders.of(headers);
    }

    public T body() {
        return body;
    }
}
