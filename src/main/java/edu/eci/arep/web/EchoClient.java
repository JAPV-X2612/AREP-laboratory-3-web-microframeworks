package edu.eci.arep.web;

import java.io.*;
import java.net.*;

/**
 * TCP echo client that connects to a local echo server on port 35000,
 * forwards user input line by line, and prints each server response.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class EchoClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 35000;

    /**
     * Application entry point. Opens a socket connection to the echo server,
     * reads lines from standard input, sends them to the server, and prints
     * each echoed response until the input stream is closed.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException If an I/O error occurs while managing streams.
     */
    public static void main(String[] args) throws IOException {
        try (
            Socket echoSocket       = openConnection();
            PrintWriter out         = new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in       = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            BufferedReader stdIn    = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Connected to echo server at " + HOST + ":" + PORT);
            System.out.println("Type a message and press Enter. Close input (Ctrl+D) to quit.");

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("Echo: " + in.readLine());
            }

            System.out.println("Session closed.");
        }
    }

    /**
     * Opens a TCP socket connection to the configured host and port.
     *
     * @return A connected {@link Socket} instance.
     * @throws IOException If the connection cannot be established.
     */
    private static Socket openConnection() throws IOException {
        try {
            return new Socket(HOST, PORT);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + HOST);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Could not establish connection to " + HOST + ":" + PORT + " — " + e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
