// File: src/main/java/org/mpad/host/controller/PairingController.java
package org.mpad.host_backend.controller;

import org.mpad.host_backend.model.TrustedDevice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for the initial TCP handshake and pairing process.
 * Before the Android app switches to high-speed UDP, it uses standard HTTP
 * to verify its identity and establish the AES encryption keys.
 */
@RestController
@RequestMapping("/api/pairing")
public class PairingController {

    // In a full implementation, you would autowire your PairingService here.
    // private final PairingService pairingService;
    // public PairingController(PairingService pairingService) { ... }

    /**
     * Endpoint for the Android app to check if the host is actively accepting 
     * new pairing requests (e.g., if the user clicked "Add Device" on the PC).
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getPairingStatus() {
        // Mocked response: Assume the host generated a PIN and is waiting.
        return ResponseEntity.ok(Map.of(
                "status", "WAITING_FOR_PIN",
                "hostName", System.getProperty("user.name") + "'s PC"
        ));
    }

    /**
     * Endpoint where the Android client submits the PIN shown on the PC monitor.
     *
     * @param request Contains "deviceId", "deviceName", and "pin"
     * @return 200 OK with the shared AES key if successful, 401 Unauthorized otherwise.
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPin(@RequestBody Map<String, String> request) {
        String deviceId = request.get("deviceId");
        String deviceName = request.get("deviceName");
        String pin = request.get("pin");

        if (deviceId == null || pin == null) {
            return ResponseEntity.badRequest().body("Missing deviceId or pin");
        }

        // TODO: Validate the PIN against the PairingService's currently active PIN.
        boolean isPinValid = true; // Mocked validation

        if (isPinValid) {
            // Generate a secure, unique AES key for this specific device pairing
            String newSharedKey = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

            TrustedDevice newDevice = new TrustedDevice(deviceId, deviceName, newSharedKey);

            // TODO: Save to trusted_devices.json via PairingService
            // pairingService.saveDevice(newDevice);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "sharedKey", newSharedKey // Send key to Android so it can encrypt UDP packets
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid PIN");
        }
    }
}