package edu.eci.arep.web;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Demonstrates reading metadata from a URL object using the eight standard accessor methods
 * provided by the java.net.URL class.
 */
public class URLInfo {

    /**
     * Application entry point. Creates a URL object and prints the values returned by each of the
     * eight URL accessor methods.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            URL url = new URL("https://www.escuelaing.edu.co/");

            System.out.println("Protocol  : " + url.getProtocol());
            System.out.println("Authority : " + url.getAuthority());
            System.out.println("Host      : " + url.getHost());
            System.out.println("Port      : " + url.getPort());
            System.out.println("Path      : " + url.getPath());
            System.out.println("Query     : " + url.getQuery());
            System.out.println("File      : " + url.getFile());
            System.out.println("Ref       : " + url.getRef());

        } catch (MalformedURLException e) {
            System.err.println("Invalid URL: " + e.getMessage());
        }
    }
}
