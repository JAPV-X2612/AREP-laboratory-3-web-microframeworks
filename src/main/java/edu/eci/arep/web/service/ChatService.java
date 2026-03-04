package edu.eci.arep.web.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface defining the contract for the RMI bidirectional chat service.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public interface ChatService extends Remote {

    /**
     * Receives a message from a remote chat node.
     *
     * @param sender  The name or address of the sending node.
     * @param message The message content.
     * @throws RemoteException If a remote communication error occurs.
     */
    void receiveMessage(String sender, String message) throws RemoteException;
}
