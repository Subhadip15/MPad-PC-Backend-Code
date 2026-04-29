// File: src/main/java/org/mpad/host/service/PairingService.java
package org.mpad.host_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mpad.host_backend.model.TrustedDevice;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages the generation of pairing PINs and persistent storage of trusted devices.
 */
@Service
public class PairingService {

    private static final String TRUSTED_DEVICES_FILE = "trusted_devices.json";
    private final ObjectMapper objectMapper;
    private final List<TrustedDevice> trustedDevices;

    private String currentActivePin = null;

    public PairingService() {
        this.objectMapper = new ObjectMapper();
        this.trustedDevices = loadTrustedDevices();
    }

    /**
     * Generates a random 6-digit PIN for pairing a new device.
     */
    public String generatePairingPin() {
        SecureRandom random = new SecureRandom();
        int pin = 100000 + random.nextInt(900000); // Guarantees 6 digits
        this.currentActivePin = String.valueOf(pin);
        return this.currentActivePin;
    }

    public boolean validatePin(String submittedPin) {
        if (currentActivePin != null && currentActivePin.equals(submittedPin)) {
            currentActivePin = null; // Invalidate PIN after single use
            return true;
        }
        return false;
    }

    public void saveDevice(TrustedDevice device) {
        // Remove old entry if this device is re-pairing
        trustedDevices.removeIf(d -> d.getDeviceId().equals(device.getDeviceId()));
        trustedDevices.add(device);
        persistToFile();
    }

    /**
     * Retrieves the shared key for a device. Useful for the UDP listener.
     */
    public Optional<String> getSharedKeyForDevice(String deviceId) {
        return trustedDevices.stream()
                .filter(d -> d.getDeviceId().equals(deviceId))
                .map(TrustedDevice::getSharedKey)
                .findFirst();
    }

    /**
     * Fallback for single-user scenarios: returns the first trusted key available.
     */
    public Optional<String> getPrimarySharedKey() {
        if (trustedDevices.isEmpty()) return Optional.empty();
        return Optional.of(trustedDevices.get(0).getSharedKey());
    }

    private List<TrustedDevice> loadTrustedDevices() {
        File file = new File(TRUSTED_DEVICES_FILE);
        if (file.exists()) {
            try {
                return objectMapper.readValue(file, new TypeReference<List<TrustedDevice>>() {});
            } catch (IOException e) {
                System.err.println("Failed to load trusted devices: " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }

    private void persistToFile() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(TRUSTED_DEVICES_FILE), trustedDevices);
        } catch (IOException e) {
            System.err.println("Failed to save trusted devices: " + e.getMessage());
        }
    }
}