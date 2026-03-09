package edu.eci.arep.web.framework;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Lightweight web framework that supports REST service registration via lambda
 * expressions and static file serving. Listens on port 8080.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class MicroSpring {

    private static final int PORT = 8080;
    private static final String APP_PREFIX = "/app";

    private static final Map<String, RestService> routes = new HashMap<>();
    private static String staticDir  = "";

    /**
     * Registers a GET route under the /app prefix.
     *
     * @param path    The route path (e.g., "/hello").
     * @param service The lambda handler for the route.
     */
    public static void get(String path, RestService service) {
        routes.put(APP_PREFIX + path, service);
    }

    /**
     * Sets the base directory for serving static files, resolved from the classpath.
     *
     * @param directory The relative path to the static files folder (e.g., "/webroot").
     */
    public static void staticfiles(String directory) {
        staticDir = "target/classes" + directory;
    }

    /**
     * Starts the HTTP server loop, handling sequential incoming requests.
     *
     * @throws IOException If an I/O error occurs while managing sockets.
     */
    public static void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("MicroSpring listening on port " + PORT + " ...");
            while (true) {
                try (Socket client = serverSocket.accept()) {
                    handleRequest(client);
                } catch (URISyntaxException e) {
                    System.err.println("Malformed URI: " + e.getMessage());
                } catch (SocketException e) {
                    System.out.println("Client disconnected: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Reads the HTTP request, routes it to a REST handler or static file, and writes the response.
     *
     * @param client The connected client socket.
     * @throws IOException        If an I/O error occurs while reading or writing streams.
     * @throws URISyntaxException If the request URI is malformed.
     */
    private static void handleRequest(Socket client) throws IOException, URISyntaxException {
        BufferedReader in  = new BufferedReader(new InputStreamReader(client.getInputStream()));
        OutputStream   out = client.getOutputStream();

        String requestLine;
        try {
            requestLine = in.readLine();
        } catch (SocketException e) {
            System.out.println("Client disconnected before sending request.");
            return;
        }

        if (requestLine == null || requestLine.isEmpty()) return;
        System.out.println("Request: " + requestLine);
        drainHeaders(in);

        String rawUri = requestLine.split(" ")[1];
        String path   = new URI(rawUri).getPath();

        if (routes.containsKey(path)) {
            serveRest(rawUri, path, out);
        } else {
            serveStaticFile(path, out);
        }
    }

    /**
     * Invokes the matching REST handler and writes the JSON/text response.
     *
     * @param rawUri The full raw URI including query string.
     * @param path   The request path used to look up the route.
     * @param out    The output stream connected to the client.
     * @throws IOException If an I/O error occurs while writing the response.
     */
    private static void serveRest(String rawUri, String path, OutputStream out) throws IOException {
        Request  req    = new Request(rawUri);
        Response res    = new Response();
        String   body   = routes.get(path).handle(req, res);

        PrintWriter writer = new PrintWriter(out, true);
        writer.print("HTTP/1.1 200 OK\r\n");
        writer.print("Content-Type: application/json\r\n");
        writer.print("Content-Length: " + body.getBytes().length + "\r\n");
        writer.print("\r\n");
        writer.print(body);
        writer.flush();

        System.out.println("REST served: " + path);
    }

    /**
     * Resolves and serves a static file from the configured static directory.
     * Responds with 404 if the file does not exist.
     *
     * @param path The request path mapped to a file in the static directory.
     * @param out  The output stream connected to the client.
     * @throws IOException If an I/O error occurs while reading or writing the file.
     */
    private static void serveStaticFile(String path, OutputStream out) throws IOException {
        String   filePath = path.equals("/") ? "/index.html" : path;
        File     file     = new File(staticDir + filePath);

        if (!file.exists() || file.isDirectory()) {
            sendNotFound(out, path);
            return;
        }

        String  contentType = resolveContentType(filePath);
        byte[]  body        = Files.readAllBytes(file.toPath());

        PrintWriter headers = new PrintWriter(out, true);
        headers.print("HTTP/1.1 200 OK\r\n");
        headers.print("Content-Type: "   + contentType + "\r\n");
        headers.print("Content-Length: " + body.length + "\r\n");
        headers.print("\r\n");
        headers.flush();

        out.write(body);
        out.flush();

        System.out.println("Static served: " + filePath);
    }

    /**
     * Sends an HTTP 404 Not Found response.
     *
     * @param out  The output stream connected to the client.
     * @param path The path that was not found.
     * @throws IOException If an I/O error occurs while writing the response.
     */
    private static void sendNotFound(OutputStream out, String path) throws IOException {
        String body      = "<h1>404 Not Found</h1><p>Resource not found: " + path + "</p>";
        byte[] bodyBytes = body.getBytes();

        PrintWriter writer = new PrintWriter(out, true);
        writer.print("HTTP/1.1 404 Not Found\r\n");
        writer.print("Content-Type: text/html\r\n");
        writer.print("Content-Length: " + bodyBytes.length + "\r\n");
        writer.print("\r\n");
        writer.flush();

        out.write(bodyBytes);
        out.flush();

        System.out.println("404 Not Found: " + path);
    }

    /**
     * Reads and discards all remaining HTTP request headers.
     *
     * @param in The buffered reader connected to the client input stream.
     * @throws IOException If an I/O error occurs while reading headers.
     */
    private static void drainHeaders(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            System.out.println("Header: " + line);
        }
    }

    /**
     * Resolves the MIME content type based on the file extension.
     *
     * @param path The file path.
     * @return The corresponding MIME type string.
     */
    private static String resolveContentType(String path) {
        if (path.endsWith(".html"))              return "text/html";
        if (path.endsWith(".css"))               return "text/css";
        if (path.endsWith(".js"))                return "application/javascript";
        if (path.endsWith(".png"))               return "image/png";
        if (path.endsWith(".jpg")
         || path.endsWith(".jpeg"))              return "image/jpeg";
        if (path.endsWith(".gif"))               return "image/gif";
        if (path.endsWith(".ico"))               return "image/x-icon";
        return "application/octet-stream";
    }
}
