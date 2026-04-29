// File: src/main/java/org/mpad/host/model/TrustedDevice.java
package org.mpad.host_backend.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an Android device that has successfully paired with this host.
 * This object is typically serialized to trusted_devices.json so the user
 * doesn't have to re-enter a PIN every time they restart their PC.
 */
public class TrustedDevice {

    private String deviceId;     // Unique identifier (e.g., Android ID or generated UUID)
    private String deviceName;   // User-friendly name (e.g., "Pixel 7 Pro")
    private String sharedKey;    // Base64 encoded AES key specific to this pairing
    private LocalDateTime pairedAt;

    public TrustedDevice() {}

    public TrustedDevice(String deviceId, String deviceName, String sharedKey) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.sharedKey = sharedKey;
        this.pairedAt = LocalDateTime.now();
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getSharedKey() { return sharedKey; }
    public void setSharedKey(String sharedKey) { this.sharedKey = sharedKey; }

    public LocalDateTime getPairedAt() { return pairedAt; }
    public void setPairedAt(LocalDateTime pairedAt) { this.pairedAt = pairedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrustedDevice that = (TrustedDevice) o;
        return Objects.equals(deviceId, that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId);
    }
}