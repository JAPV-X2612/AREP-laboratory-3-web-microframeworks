package edu.eci.arep.web.util;

import java.io.*;
import java.net.*;

/**
 * Reads and prints the HTML content of a remote web page using the java.net.URL class and a
 * buffered character stream.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class URLReader {

    /**
     * Opens an HTTP connection to Google's homepage, reads the response body line by line, and
     * prints each line to standard output.
     *
     * @param args Command-line arguments (not used)
     * @throws IOException If the URL is malformed or the connection cannot be established
     */
    public static void main(String[] args) throws IOException {
        URL google = new URL("http://www.google.com/");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(google.openStream()))) {
            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);
            }
        } catch (IOException x) {
            System.err.println(x);
        }
    }
}
