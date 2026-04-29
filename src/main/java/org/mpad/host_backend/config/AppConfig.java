// File: src/main/java/org/mpad/host/config/AppConfig.java
package org.mpad.host_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AppConfig implements WebMvcConfigurer {

    /**
     * Configure a thread pool specifically for background networking tasks.
     * This prevents our while(true) UDP socket loops from freezing the main application.
     */
    @Bean(name = "networkTaskExecutor")
    public Executor networkTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // One for UDP Data, one for UDP Discovery
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("MpadNet-");
        executor.initialize();
        return executor;
    }

    /**
     * Allows the Android app (or a potential web frontend) to communicate 
     * with the TCP REST endpoints (like /api/pairing/verify).
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*") // In production LAN, exact IPs are unpredictable
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}