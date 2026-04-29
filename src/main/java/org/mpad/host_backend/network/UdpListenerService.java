package org.mpad.host_backend.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.mpad.host_backend.model.CommandPayload;
import org.mpad.host_backend.service.CommandProcessorService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Service
public class UdpListenerService {

    private final CommandProcessorService processor;
    private final ObjectMapper mapper = new ObjectMapper();

    public UdpListenerService(CommandProcessorService processor) {
        this.processor = processor;
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ [STATUS] UdpListenerService Bean Created");
    }

    @Async("networkTaskExecutor")
    @EventListener(ApplicationReadyEvent.class)
    public void startListening() {
        System.out.println("🚀 [NETWORK] UDP Mouse Listener starting on port 8888...");

        try (DatagramSocket socket = new DatagramSocket(8888)) {
            byte[] buffer = new byte[2048];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); // Thread waits here for phone data

                String rawJson = new String(packet.getData(), 0, packet.getLength()).trim();

                // If you see this, the connection is 100% working!
                System.out.println("📥 [RECEIVED]: " + rawJson);

                try {
                    CommandPayload payload = mapper.readValue(rawJson, CommandPayload.class);
                    processor.process(payload);
                } catch (Exception e) {
                    System.err.println("❌ [JSON ERROR]: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("🚨 [CRITICAL ERROR]: Could not open socket 8888: " + e.getMessage());
        }
    }
}