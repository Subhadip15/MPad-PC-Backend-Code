// File: src/main/java/org/mpad/host/security/RateLimiter.java
package org.mpad.host_backend.security;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Prevents UDP flood attacks and spam by limiting the number of packets 
 * processed per second. Uses a thread-safe sliding window counter.
 */
@Component
public class RateLimiter {
    // 200 packets/sec allows for a very smooth 5ms polling rate (like a gaming mouse)
    private static final int MAX_PACKETS_PER_SECOND = 200;

    private volatile long windowStartTime;
    private final AtomicInteger packetCount;

    public RateLimiter() {
        this.windowStartTime = System.currentTimeMillis();
        this.packetCount = new AtomicInteger(0);
    }

    /**
     * Checks if the incoming packet should be allowed.
     * @return true if under limit, false to drop packet.
     */
    public boolean allowRequest() {
        long now = System.currentTimeMillis();

        // Reset the window every second
        if (now - windowStartTime > 1000) {
            windowStartTime = now;
            packetCount.set(0);
        }

        return packetCount.incrementAndGet() <= MAX_PACKETS_PER_SECOND;
    }
}