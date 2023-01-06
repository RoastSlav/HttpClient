import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;

public class HttpRequest {
    static class BodyPublishers {
        //TODO save the inputstream in a field and remove the body array
        private byte[] body = null;

        public HttpRequest.BodyPublishers noBody() {
            return this;
        }

        public HttpRequest.BodyPublishers ofString(String s) {
            body = s.getBytes();
            return this;
        }

        public HttpRequest.BodyPublishers ofByteArray(byte[] bytes) {
            body = bytes;
            return this;
        }

        public HttpRequest.BodyPublishers ofInputStream(Supplier<? extends InputStream> streamSupplier) {
            ArrayList<Byte> bytes = new ArrayList<>();
            InputStream stream = streamSupplier.get();
            try {
                for (int readBytes; (readBytes = stream.read()) != -1; ) {
                    bytes.add((byte) readBytes);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            body = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) {
                body[i] = bytes.get(i);
            }
            return this;
        }

        public HttpRequest.BodyPublishers fromFile(Path path) {
            ArrayList<Byte> bytes = new ArrayList<>();
            try (InputStream stream = path.toUri().toURL().openStream()) {
                for (int readBytes; (readBytes = stream.read()) != -1; ) {
                    bytes.add((byte) readBytes);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            body = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) {
                body[i] = bytes.get(i);
            }
            return this;
        }
    }

    static class Builder {
        HttpRequest request;

        Builder uri(URI uri) throws IllegalArgumentException {
            try {
                request.url = uri.toURL();
            } catch (Exception e) {
                throw new IllegalArgumentException();
            }

            return this;
        }

        Builder header(String name, String value) {
            request.headers.put(name, value);
            return this;
        }

        Builder headers(String... headers) throws IllegalArgumentException {
            if (headers.length % 2 != 0) {
                throw new IllegalArgumentException("Headers must be in pairs");
            }

            for (int i = 0; i < headers.length; i += 2) {
                request.headers.put(headers[i], headers[i + 1]);
            }
            return this;
        }

        Builder GET() {
            request.method = "GET";
            return this;
        }

        Builder POST(HttpRequest.BodyPublishers publisher) {
            request.method = "POST";
            request.body = publisher.body;
            request.headers.put("Content-Length", String.valueOf(request.body.length));
            return this;
        }

        HttpRequest build() {
            return request;
        }

        private Builder clean() {
            request = new HttpRequest();
            return this;
        }
    }

    private static final Builder builder = new Builder();
    public static HttpRequest.BodyPublishers BodyPublishers = new BodyPublishers();
    protected final HashMap<String, String> headers = new HashMap<String, String>();
    protected URL url = null;
    protected String method = null;
    protected byte[] body = null;

    public static Builder newBuilder() {
        return builder.clean();
    }
}
