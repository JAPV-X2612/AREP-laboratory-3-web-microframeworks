package edu.eci.arep.web.server;

import java.io.*;
import java.net.*;

/**
 * TCP server that receives a number from a client and responds with its square.
 * Listens on port 35000 and handles one client at a time sequentially.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class SquareServer {

    private static final int PORT = 35000;
    private static final String EXIT_COMMAND = "Bye.";

    /**
     * Starts the server and handles incoming client sessions.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException If an I/O error occurs while managing sockets.
     */
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Square server listening on port " + PORT + " ...");
            while (true) {
                try (Socket client = serverSocket.accept()) {
                    handleSession(client);
                }
            }
        }
    }

    /**
     * Reads numbers from the client and responds with their squares.
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
            System.out.println("Client connected: " + client.getInetAddress());
            String input;
            while ((input = in.readLine()) != null) {
                if (input.equals(EXIT_COMMAND)) break;
                out.println(computeSquare(input));
            }
        } catch (SocketException e) {
            System.out.println("Client disconnected abruptly: " + e.getMessage());
        }
    }

    /**
     * Parses the input string as a double and returns its square.
     * Returns an error message if the input is not a valid number.
     *
     * @param input The raw string received from the client.
     * @return The square of the parsed number, or an error message.
     */
    private static String computeSquare(String input) {
        try {
            double number = Double.parseDouble(input);
            return "Result: " + (number * number);
        } catch (NumberFormatException e) {
            return "Error: '" + input + "' is not a valid number.";
        }
    }
}
