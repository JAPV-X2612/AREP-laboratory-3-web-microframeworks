package edu.eci.arep.web.client;

import edu.eci.arep.web.service.EchoService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * RMI client that connects to the EchoService registry and invokes the remote echo method.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class EchoServiceClient {

    /**
     * Locates the EchoService in the RMI registry and invokes the echo method.
     *
     * @param registryHost The IP address of the RMI registry.
     * @param registryPort The port on which the RMI registry is listening.
     * @param serviceName  The name under which the service is registered.
     */
    public void invokeService(String registryHost, int registryPort, String serviceName) {
        try {
            Registry registry = LocateRegistry.getRegistry(registryHost, registryPort);
            EchoService echoService = (EchoService) registry.lookup(serviceName);
            System.out.println(echoService.echo("Hello, how are you?"));
        } catch (Exception e) {
            System.err.println("Failed to invoke remote service: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Starts the RMI echo client.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new EchoServiceClient().invokeService("127.0.0.1", 23000, "echoService");
    }
}
