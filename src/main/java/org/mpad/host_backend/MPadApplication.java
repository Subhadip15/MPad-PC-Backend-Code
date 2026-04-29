package org.mpad.host_backend;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class MPadApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(MPadApplication.class)
				.headless(false)
				.run(args);
		System.out.println("🚀 Mpad Host is ready. Connect your phone!");
	}
}