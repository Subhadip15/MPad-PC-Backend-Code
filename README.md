# Mpad - Host Backend

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot-brightgreen?logo=springboot)
![Windows](https://img.shields.io/badge/OS-Windows-blue?logo=windows)

### The high-performance bridge between Mpad Android and Windows OS.

This Spring Boot application acts as the server-side host for Mpad Remote. It listens for UDP packets containing gesture data and executes them at the OS level using the Java Robot API.

## # FEATURES
* **UDP Command Listener:** High-frequency socket bound to Port 8888.
* **Fractional Scroll Accumulator:** Logic to ensure smooth scrolling even with small touch deltas.
* **Robot Input Injection:** Direct hardware-level simulation for mouse and keyboard.
* **Low Latency:** Optimized packet processing for real-time responsiveness.

## # ENGINEERING CHALLENGES SOLVED
* **Click Reliability:** Implemented micro-delays between mouse events to ensure Windows OS registration.
* **Smooth Movement:** Sub-pixel rounding for 1:1 cursor parity.
* **Keyboard Logic:** Character-to-keycode mapping including Shift-state handling for symbols.

## # SETUP
1. Ensure **Java 17+** is installed.
2. Build the project:
   ```bash
   mvn clean install
