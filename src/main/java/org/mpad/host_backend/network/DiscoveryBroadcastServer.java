// File: src/main/java/org/mpad/host/network/DiscoveryBroadcastServer.java
package org.mpad.host_backend.network;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

@Service
public class DiscoveryBroadcastServer {

    private static final int DISCOVERY_PORT = 8889;
    private static final String DISCOVERY_REQUEST_MESSAGE = "MPAD_DISCOVERY_REQUEST";

    /**
     * Starts automatically when Spring Boot finishes loading.
     * Listens on port 8889 specifically for UDP broadcasts.
     */
    @Async("networkTaskExecutor")
    @EventListener(ApplicationReadyEvent.class)
    public void startBroadcastListener() {
        try (DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT)) {
            socket.setBroadcast(true);
            byte[] buffer = new byte[256];

            System.out.println("[Mpad] Discovery Server listening on port " + DISCOVERY_PORT);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();

                // If an Android device is scanning for hosts...
                if (DISCOVERY_REQUEST_MESSAGE.equals(message)) {

                    // Reply with our hostname so it looks nice in the app's UI
                    String hostName = System.getProperty("user.name") + "'s PC";
                    String responseData = "MPAD_HOST_INFO:" + hostName;

                    byte[] responseBytes = responseData.getBytes(StandardCharsets.UTF_8);

                    // Send the response back to the exact IP and Port that asked for it
                    DatagramPacket responsePacket = new DatagramPacket(
                            responseBytes,
                            responseBytes.length,
                            packet.getAddress(),
                            packet.getPort()
                    );

                    socket.send(responsePacket);
                }
            }
        } catch (Exception e) {
            System.err.println("[Mpad] Discovery Server failed: " + e.getMessage());
        }
    }
}