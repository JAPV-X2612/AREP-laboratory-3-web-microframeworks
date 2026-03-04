package edu.eci.arep.web.client;

import edu.eci.arep.web.service.ChatService;
import edu.eci.arep.web.service.impl.ChatServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Bidirectional RMI chat node that acts as both server and client simultaneously.
 * Registers itself in a local RMI registry to receive messages, and connects to
 * a remote node to send messages concurrently.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class ChatNode {

    private static final String SERVICE_NAME = "chatService";

    /**
     * Starts the chat node. Prompts for local port, remote host, and remote port,
     * then launches a listener thread and an input loop for sending messages.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter local registry port      : ");
        int localPort = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter remote host IP           : ");
        String remoteHost = scanner.nextLine().trim();

        System.out.print("Enter remote registry port     : ");
        int remotePort = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter your name                : ");
        String senderName = scanner.nextLine().trim();

        try {
            new ChatServiceImpl(localPort, SERVICE_NAME);
            System.out.println("Listening for messages on port " + localPort + " ...");

            ChatService remoteNode = connectToRemote(remoteHost, remotePort);

            startSendingLoop(scanner, remoteNode, senderName);

        } catch (Exception e) {
            System.err.println("Chat node error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Connects to a remote chat node via the RMI registry.
     *
     * @param host       The IP address of the remote registry.
     * @param port       The port of the remote registry.
     * @return           A remote stub of the ChatService.
     * @throws Exception If the registry lookup fails.
     */
    private static ChatService connectToRemote(String host, int port) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, port);
        ChatService remoteNode = (ChatService) registry.lookup(SERVICE_NAME);
        System.out.println("Connected to remote node at " + host + ":" + port);
        return remoteNode;
    }

    /**
     * Reads messages from standard input and sends them to the remote chat node.
     * Stops when the user types "bye".
     *
     * @param scanner    The scanner reading from standard input.
     * @param remoteNode The remote ChatService stub.
     * @param senderName The display name of the local user.
     */
    private static void startSendingLoop(Scanner scanner, ChatService remoteNode, String senderName) {
        System.out.println("Start chatting (type 'bye' to quit):");
        try {
            String input;
            while (!(input = scanner.nextLine()).equalsIgnoreCase("bye")) {
                remoteNode.receiveMessage(senderName, input);
            }
            remoteNode.receiveMessage(senderName, "** has left the chat **");
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}
