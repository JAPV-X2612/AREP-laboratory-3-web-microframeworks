package edu.eci.arep.web;

import java.net.*;
import java.io.*;

/**
 * Simple single-threaded HTTP server that listens on port 35000, handles sequential client
 * requests, and returns a static HTML response.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class HttpServer {

    /** Port on which the server listens for incoming connections. */
    private static final int PORT = 35000;

    /**
     * Application entry point. Starts the HTTP server, continuously accepts client connections,
     * parses the request line, and sends a static HTML response.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException If an I/O error occurs while reading or writing streams.
     * @throws URISyntaxException If the request URI cannot be parsed.
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT);
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = acceptConnection(serverSocket);
            handleRequest(clientSocket);
        }

        serverSocket.close();
    }

    /**
     * Waits for and accepts an incoming client connection.
     *
     * @param serverSocket The server socket listening for connections.
     * @return The accepted client socket.
     */
    private static Socket acceptConnection(ServerSocket serverSocket) {
        Socket clientSocket = null;
        try {
            System.out.println("Ready to receive connections on port " + PORT + " ...");
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Failed to accept connection: " + e.getMessage());
            System.exit(1);
        }
        return clientSocket;
    }

    /**
     * Reads the HTTP request from the client socket, parses the request line, and sends a static
     * HTML response.
     *
     * @param clientSocket The connected client socket.
     * @throws IOException If an I/O error occurs while handling the request.
     * @throws URISyntaxException If the request URI is malformed.
     */
    private static void handleRequest(Socket clientSocket) throws IOException, URISyntaxException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in =
                new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        boolean firstLine = true;
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (firstLine) {
                parseRequestLine(inputLine);
                firstLine = false;
            }
            if (!in.ready()) {
                break;
            }
        }

        out.println(buildResponse());

        out.close();
        in.close();
        clientSocket.close();
    }

    /**
     * Parses the HTTP request line and logs the method, URI, protocol, and path.
     *
     * @param requestLine The first line of the HTTP request (e.g., "GET /index.html HTTP/1.1").
     * @throws URISyntaxException If the URI segment of the request line is malformed.
     */
    private static void parseRequestLine(String requestLine) throws URISyntaxException {
        String[] tokens = requestLine.split(" ");
        String method = tokens[0];
        String rawUri = tokens[1];
        String protocol = tokens[2];
        URI uri = new URI(rawUri);

        System.out.println("Method   : " + method);
        System.out.println("URI      : " + rawUri);
        System.out.println("Protocol : " + protocol);
        System.out.println("Path     : " + uri.getPath());
    }

    /**
     * Builds a minimal HTTP/1.1 200 OK response with a static HTML body.
     *
     * @return The full HTTP response string.
     */
    private static String buildResponse() {
        return "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n" + "<!DOCTYPE html>"
                + "<html>" + "<head>" + "<meta charset=\"UTF-8\">" + "<title>My Web Server</title>"
                + "</head>" + "<body>" + "<h1>Welcome to My Web Server</h1>" + "</body>"
                + "</html>";
    }
}
