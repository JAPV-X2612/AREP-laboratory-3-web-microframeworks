package edu.eci.arep.web.framework;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an incoming HTTP request, providing access to the request path
 * and query parameters.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class Request {

    private final String path;
    private final Map<String, String> queryParams;

    /**
     * Constructs a Request by parsing the raw URI into path and query parameters.
     *
     * @param rawUri The raw URI from the HTTP request line (e.g., /app/hello?name=Pedro).
     */
    public Request(String rawUri) {
        String[] parts = rawUri.split("\\?", 2);
        this.path        = parts[0];
        this.queryParams = parts.length > 1 ? parseQuery(parts[1]) : Collections.emptyMap();
    }

    /**
     * Returns the value of the specified query parameter.
     *
     * @param key The query parameter name.
     * @return The parameter value, or null if not present.
     */
    public String getValues(String key) {
        return queryParams.get(key);
    }

    /**
     * Returns the request path without query string.
     *
     * @return The request path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Parses a query string into a key-value map.
     *
     * @param query The raw query string (e.g., name=Pedro&age=30).
     * @return A map of query parameter names to values.
     */
    private Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(kv[0], kv[1]);
            }
        }
        return params;
    }
}
