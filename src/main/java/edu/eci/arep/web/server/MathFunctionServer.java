package edu.eci.arep.web.server;

import java.io.*;
import java.net.*;
import java.util.function.DoubleUnaryOperator;
import java.util.Map;

/**
 * TCP server that applies a trigonometric function to received numbers.
 * Supports sine, cosine, and tangent. The active function defaults to cosine
 * and can be changed at runtime by sending "fun:sin", "fun:cos", or "fun:tan".
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class MathFunctionServer {

    private static final int PORT = 35000;
    private static final String FUN_PREFIX = "fun:";
    private static final String EXIT_COMMAND = "Bye.";

    private static final Map<String, DoubleUnaryOperator> FUNCTIONS = Map.of(
        "sin", Math::sin,
        "cos", Math::cos,
        "tan", Math::tan
    );

    /**
     * Starts the server and handles incoming client sessions.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException If an I/O error occurs while managing sockets.
     */
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Math function server listening on port " + PORT + " ...");
            while (true) {
                try (Socket client = serverSocket.accept()) {
                    handleSession(client);
                }
            }
        }
    }

    /**
     * Manages a client session, processing number inputs and function-switch commands.
     * The active function resets to cosine for each new client connection.
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
            DoubleUnaryOperator activeFunction = FUNCTIONS.get("cos");
            String[] activeName = {"cos"};

            String input;
            while ((input = in.readLine()) != null) {
                if (input.equals(EXIT_COMMAND)) break;

                if (input.startsWith(FUN_PREFIX)) {
                    String requested = input.substring(FUN_PREFIX.length()).trim();
                    if (FUNCTIONS.containsKey(requested)) {
                        activeFunction = FUNCTIONS.get(requested);
                        activeName[0]  = requested;
                        out.println("Function changed to: " + requested);
                    } else {
                        out.println("Error: unknown function '" + requested + "'. Use sin, cos or tan.");
                    }
                } else {
                    out.println(applyFunction(input, activeFunction, activeName[0]));
                }
            }
        } catch (SocketException e) {
            System.out.println("Client disconnected abruptly: " + e.getMessage());
        }
    }

    /**
     * Parses the input as a double and applies the active trigonometric function.
     *
     * @param input          The raw string received from the client.
     * @param function       The currently active trigonometric function.
     * @param functionName   The name of the active function, used in the response label.
     * @return The function result as a formatted string, or an error message.
     */
    private static String applyFunction(String input, DoubleUnaryOperator function, String functionName) {
        try {
            double number = Double.parseDouble(input);
            return functionName + "(" + number + ") = " + function.applyAsDouble(number);
        } catch (NumberFormatException e) {
            return "Error: '" + input + "' is not a valid number.";
        }
    }
}
