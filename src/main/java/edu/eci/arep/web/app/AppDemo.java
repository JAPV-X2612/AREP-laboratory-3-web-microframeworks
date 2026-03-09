package edu.eci.arep.web.app;

import edu.eci.arep.web.framework.MicroSpring;

import java.io.IOException;

/**
 * Demonstration application showing how to use the MicroSpring framework
 * to define REST services and serve static files.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class AppDemo {

    /**
     * Starts the MicroSpring server with two REST endpoints and a static files directory.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException If the server fails to start.
     */
    public static void main(String[] args) throws IOException {
        MicroSpring.staticfiles("/webroot");
        MicroSpring.get("/hello", (req, res) -> "Hello " + req.getValues("name"));
        MicroSpring.get("/pi",    (req, res) -> String.valueOf(Math.PI));
        MicroSpring.start();
    }
}
