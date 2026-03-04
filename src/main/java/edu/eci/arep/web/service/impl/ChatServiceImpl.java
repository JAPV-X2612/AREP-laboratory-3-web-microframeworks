package edu.eci.arep.web.service.impl;

import edu.eci.arep.web.service.ChatService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * RMI implementation of the ChatService interface. Prints incoming messages
 * to standard output as they are received from remote nodes.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class ChatServiceImpl implements ChatService {

    /**
     * Exports this instance and binds it to the given RMI registry.
     *
     * @param registryPort The port on which the local RMI registry is listening.
     * @param serviceName  The name under which this node registers itself.
     * @throws Exception If the export or registry binding fails.
     */
    public ChatServiceImpl(int registryPort, String serviceName) throws Exception {
        ChatService stub = (ChatService) UnicastRemoteObject.exportObject(this, 0);
        Registry registry = LocateRegistry.createRegistry(registryPort);
        registry.rebind(serviceName, stub);
        System.out.println("Chat node registered as '" + serviceName + "' on port " + registryPort);
    }

    /**
     * Receives and prints an incoming chat message.
     *
     * @param sender  The name or address of the sending node.
     * @param message The message content.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public void receiveMessage(String sender, String message) throws RemoteException {
        System.out.println("[" + sender + "]: " + message);
    }
}
