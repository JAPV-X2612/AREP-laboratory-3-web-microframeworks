package edu.eci.arep.web;

import java.io.*;
import java.net.*;
import java.nio.file.*;

/**
 * Single-threaded HTTP server that handles sequential requests and serves static
 * files (HTML, CSS, JS, PNG, JPG, GIF, ICO) from the resources/static directory.
 *
 * @author Jesús Pinzón
 * @version 2.0
 * @since 2026-02-24
 */
public class HttpServer {

    private static final int    PORT        = 35000;
    private static final String STATIC_DIR  = "src/main/resources/static";

    /**
     * Starts the HTTP server and continuously handles sequential client requests.
     *
     * @param args           Command-line arguments (not used).
     * @throws IOException   If an I/O error occurs while managing sockets.
     */
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("HTTP server listening on port " + PORT + " ...");
            while (true) {
                try (Socket client = serverSocket.accept()) {
                    handleRequest(client);
                } catch (URISyntaxException e) {
                    System.err.println("Malformed request URI: " + e.getMessage());
                } catch (SocketException e) {
                    System.out.println("Client disconnected abruptly: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("I/O error handling request: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Reads the HTTP request, resolves the requested file, and sends the appropriate response.
     *
     * @param client                The connected client socket.
     * @throws IOException          If an I/O error occurs while reading or writing streams.
     * @throws URISyntaxException   If the request URI is malformed.
     */
    private static void handleRequest(Socket client) throws IOException, URISyntaxException {
        BufferedReader  in  = new BufferedReader(new InputStreamReader(client.getInputStream()));
        OutputStream    out = client.getOutputStream();

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

        String path = extractPath(requestLine);
        serveFile(path, out);
    }

    /**
     * Reads and discards all remaining HTTP request headers.
     *
     * @param in             The buffered reader connected to the client input stream.
     * @throws IOException   If an I/O error occurs while reading headers.
     */
    private static void drainHeaders(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            System.out.println("Header: " + line);
        }
    }

    /**
     * Extracts and normalizes the request path from the HTTP request line.
     * Redirects root requests to index.html.
     *
     * @param requestLine           The first line of the HTTP request.
     * @return                      The normalized file path.
     * @throws URISyntaxException   If the URI segment is malformed.
     */
    private static String extractPath(String requestLine) throws URISyntaxException {
        String rawUri = requestLine.split(" ")[1];
        String path   = new URI(rawUri).getPath();
        return path.equals("/") ? "/index.html" : path;
    }

    /**
     * Resolves the requested file from the static directory and writes the HTTP response.
     * Responds with 404 if the file does not exist.
     *
     * @param path           The normalized request path.
     * @param out            The output stream connected to the client.
     * @throws IOException   If an I/O error occurs while writing the response.
     */
    private static void serveFile(String path, OutputStream out) throws IOException {
        File file = new File(STATIC_DIR + path);

        if (!file.exists() || file.isDirectory()) {
            sendNotFound(out, path);
            return;
        }

        String contentType = resolveContentType(path);
        byte[] body        = Files.readAllBytes(file.toPath());

        PrintWriter headers = new PrintWriter(out, true);
        headers.print("HTTP/1.1 200 OK\r\n");
        headers.print("Content-Type: "   + contentType + "\r\n");
        headers.print("Content-Length: " + body.length + "\r\n");
        headers.print("\r\n");
        headers.flush();

        out.write(body);
        out.flush();

        System.out.println("Served: " + path + " [" + contentType + "]");
    }

    /**
     * Sends an HTTP 404 Not Found response to the client.
     *
     * @param out            The output stream connected to the client.
     * @param path           The path that was not found, included in the response body.
     * @throws IOException   If an I/O error occurs while writing the response.
     */
    private static void sendNotFound(OutputStream out, String path) throws IOException {
        String body     = "<h1>404 Not Found</h1><p>Resource not found: " + path + "</p>";
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
     * Resolves the MIME content type based on the file extension.
     *
     * @param path   The file path whose extension determines the content type.
     * @return       The corresponding MIME type string.
     */
    private static String resolveContentType(String path) {
        if (path.endsWith(".html"))       return "text/html";
        if (path.endsWith(".css"))        return "text/css";
        if (path.endsWith(".js"))         return "application/javascript";
        if (path.endsWith(".png"))        return "image/png";
        if (path.endsWith(".jpg")
         || path.endsWith(".jpeg"))       return "image/jpeg";
        if (path.endsWith(".gif"))        return "image/gif";
        if (path.endsWith(".ico"))        return "image/x-icon";
        return "application/octet-stream";
    }
}
