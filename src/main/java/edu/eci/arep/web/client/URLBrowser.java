package edu.eci.arep.web.client;

import java.io.*;
import java.net.*;

/**
 * Simple command-line browser that prompts the user for a URL, fetches its
 * HTML content, and saves the response body to {@code resultado.html}.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class URLBrowser {

    private static final String OUTPUT_FILE = "result.html";

    /**
     * Application entry point. Reads a URL from standard input, fetches its
     * content, and writes it to {@value #OUTPUT_FILE}.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try (BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter a URL (e.g. http://www.example.com): ");
            String rawUrl = stdIn.readLine().trim();

            String content = fetchContent(rawUrl);
            saveToFile(content, OUTPUT_FILE);

            System.out.println("Content saved to: " + OUTPUT_FILE);

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }

    /**
     * Opens a connection to the given URL and reads its full response body.
     *
     * @param rawUrl The URL string to fetch.
     * @return The full HTML content of the response as a single string.
     * @throws IOException           If the connection or stream fails.
     * @throws MalformedURLException If the provided URL string is invalid.
     */
    private static String fetchContent(String rawUrl) throws IOException {
        URL url = new URL(rawUrl);
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }

        return content.toString();
    }

    /**
     * Writes the given content string to a file with the specified name.
     *
     * @param content  The HTML content to write.
     * @param fileName The name of the output file.
     * @throws IOException If the file cannot be created or written.
     */
    private static void saveToFile(String content, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        }
    }
}
