package edu.eci.arep.web;

import java.io.*;
import java.net.*;

/**
 * Single-client TCP echo server that listens on port 35000, receives messages
 * from a client, and responds with each message prefixed by "Response: ".
 * The session ends when the client sends "Bye.".
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class EchoServer {

    private static final int PORT = 35000;
    private static final String EXIT_COMMAND = "Bye.";

    /**
     * Starts the echo server and handles a single client session.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException If an I/O error occurs while managing sockets.
     */
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Echo server listening on port " + PORT + " ...");
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                handleSession(clientSocket);
            }
        }
    }

    /**
     * Reads messages from the client and responds with each one prefixed by "Response: ".
     * Closes the session when the exit command is received.
     *
     * @param client The connected client socket.
     * @throws IOException If an I/O error occurs while reading or writing streams.
     */
    private static void handleSession(Socket client) throws IOException {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out   = new PrintWriter(client.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Message received: " + inputLine);
                String outputLine = "Response: " + inputLine;
                out.println(outputLine);
                if (inputLine.equals(EXIT_COMMAND)) {
                    System.out.println("Exit command received. Closing session.");
                    break;
                }
            }
        } catch (SocketException e) {
            System.out.println("Client disconnected abruptly: " + e.getMessage());
        }
    }
}
