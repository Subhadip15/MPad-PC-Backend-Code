// File: src/main/java/org/mpad/host/security/CryptoUtil.java
package org.mpad.host_backend.security;

import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * Handles AES-128 CBC encryption and decryption.
 */
@Component
public class CryptoUtil {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * Decrypts the incoming UDP packet using the provided shared key.
     *
     * @param encryptedPacket The raw byte array from the UDP socket.
     * @param length The length of the received data.
     * @param base64Key The shared AES key established during TCP pairing.
     * @return The decrypted JSON string.
     */
    public String decrypt(byte[] encryptedPacket, int length, String base64Key) throws Exception {
        if (length <= 16) throw new IllegalArgumentException("Packet too short to contain IV");

        // Extract the 16-byte IV prepended by the Android client
        byte[] iv = Arrays.copyOfRange(encryptedPacket, 0, 16);
        byte[] cipherText = Arrays.copyOfRange(encryptedPacket, 16, length);

        // Decode the shared key
        byte[] keyBytes = base64Key.getBytes(StandardCharsets.UTF_8);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(cipherText);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}