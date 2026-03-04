package edu.eci.arep.web.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface defining the contract for the RMI echo service.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public interface EchoService extends Remote {

    /**
     * Returns the given message echoed back from the remote server.
     *
     * @param message The string to echo.
     * @return The echoed message prefixed with a server label.
     * @throws RemoteException If a remote communication error occurs.
     */
    String echo(String message) throws RemoteException;
}
