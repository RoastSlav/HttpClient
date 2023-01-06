import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpHeaders {
    private final HashMap<String, String> headers = new HashMap<>();

    public HttpHeaders() {
    }

    private HttpHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    protected HttpHeaders add(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public Optional<String> firstValue(String key) {
        return Optional.of(headers.get(key));
    }

    public List<String> allValues(String key) {
        return headers.entrySet()
                .stream()
                .filter(e -> e.getKey().equals(key))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public Map<String, String> map() {
        return new HashMap<>(headers);
    }

    public String toString() {
        return headers.toString();
    }

    public static HttpHeaders of(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders(headers);
        return httpHeaders;
    }
}
