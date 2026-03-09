package edu.eci.arep.web.framework;

/**
 * Functional interface representing a REST service handler using lambda expressions.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
@FunctionalInterface
public interface RestService {

    /**
     * Handles an HTTP request and returns a response body string.
     *
     * @param req The incoming HTTP request.
     * @param res The HTTP response placeholder.
     * @return The response body as a string.
     */
    String handle(Request req, Response res);
}
