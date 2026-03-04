package edu.eci.arep.web.service.impl;

import edu.eci.arep.web.service.EchoService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * RMI implementation of the EchoService interface.
 * Exports itself as a remote object and registers it in the RMI registry under a given service name.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class EchoServiceImpl implements EchoService {

    /**
     * Exports this instance as a remote object and binds it to the RMI registry.
     *
     * @param registryHost    The IP address of the RMI registry.
     * @param registryPort    The port on which the RMI registry is listening.
     * @param serviceName     The name under which the service is registered.
     */
    public EchoServiceImpl(String registryHost, int registryPort, String serviceName) {
        try {
            EchoService stub = (EchoService) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry(registryHost, registryPort);
            registry.rebind(serviceName, stub);
            System.out.println("Echo service registered as '" + serviceName + "' on " + registryHost + ":" + registryPort);
        } catch (Exception e) {
            System.err.println("Failed to register echo service: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns the received message prefixed with a server label.
     *
     * @param message The string to echo.
     * @return The echoed message prefixed with "From server: ".
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public String echo(String message) throws RemoteException {
        return "From server: " + message;
    }

    /**
     * Starts the RMI echo service.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new EchoServiceImpl("127.0.0.1", 23000, "echoService");
    }
}
