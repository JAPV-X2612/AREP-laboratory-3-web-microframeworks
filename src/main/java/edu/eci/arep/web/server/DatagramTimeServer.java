package edu.eci.arep.web.server;

import java.io.IOException;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * UDP server that listens for incoming datagrams and responds with the current
 * server time. Runs continuously until manually stopped.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class DatagramTimeServer {

    private static final int    PORT              = 45000;
    private static final int    BUFFER_SIZE       = 256;
    private static final String TIME_FORMAT       = "HH:mm:ss";

    /**
     * Starts the UDP time server and responds to each incoming request
     * with the current server time.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("UDP Time Server listening on port " + PORT + " ...");
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            while (true) {
                handleRequest(socket);
            }
        } catch (SocketException e) {
            System.err.println("Could not open UDP socket on port " + PORT + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }

    /**
     * Waits for a single incoming datagram and responds with the current server time.
     *
     * @param socket The active datagram socket.
     * @throws IOException If an I/O error occurs while receiving or sending datagrams.
     */
    private static void handleRequest(DatagramSocket socket) throws IOException {
        byte[]         buf    = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern(TIME_FORMAT));
        byte[] response    = currentTime.getBytes();

        DatagramPacket reply = new DatagramPacket(
            response,
            response.length,
            packet.getAddress(),
            packet.getPort()
        );
        socket.send(reply);
        System.out.println("Time sent: " + currentTime + " -> " + packet.getAddress());
    }
}
