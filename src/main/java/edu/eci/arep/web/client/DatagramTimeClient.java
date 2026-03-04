package edu.eci.arep.web.client;

import java.io.IOException;
import java.net.*;

/**
 * UDP client that requests the current time from a datagram time server every 5 seconds.
 * If a response is not received within the timeout period, the last known time is retained
 * and displayed until the server becomes available again.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-02-24
 */
public class DatagramTimeClient {

    private static final String SERVER_HOST    = "127.0.0.1";
    private static final int    SERVER_PORT    = 45000;
    private static final int    BUFFER_SIZE    = 256;
    private static final int    INTERVAL_MS    = 5000;
    private static final int    TIMEOUT_MS     = 3000;

    /**
     * Starts the time client loop. Requests the server time every 5 seconds,
     * retaining the last known time if the server is unreachable.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        String lastKnownTime = "N/A";

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);

            System.out.println("UDP Time Client started. Polling every " + INTERVAL_MS / 1000 + "s ...");

            while (true) {
                lastKnownTime = requestTime(socket, serverAddress, lastKnownTime);
                System.out.println("Current time: " + lastKnownTime);
                Thread.sleep(INTERVAL_MS);
            }

        } catch (SocketException | UnknownHostException e) {
            System.err.println("Socket error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Client interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Sends a time request to the server and returns the received time string.
     * If the server is unreachable or times out, returns the last known time.
     *
     * @param socket        The active datagram socket.
     * @param serverAddress The resolved server IP address.
     * @param lastKnownTime The last successfully received time string.
     * @return The updated time string, or the last known time if unavailable.
     */
    private static String requestTime(DatagramSocket socket, InetAddress serverAddress, String lastKnownTime) {
        try {
            byte[]         sendBuf    = new byte[1];
            DatagramPacket request    = new DatagramPacket(sendBuf, sendBuf.length, serverAddress, SERVER_PORT);
            socket.send(request);

            byte[]         recvBuf    = new byte[BUFFER_SIZE];
            DatagramPacket response   = new DatagramPacket(recvBuf, recvBuf.length);
            socket.receive(response);

            return new String(response.getData(), 0, response.getLength());

        } catch (SocketTimeoutException e) {
            System.out.println("Server unreachable - retaining last known time.");
            return lastKnownTime;
        } catch (IOException e) {
            System.out.println("I/O error - retaining last known time: " + e.getMessage());
            return lastKnownTime;
        }
    }
}
