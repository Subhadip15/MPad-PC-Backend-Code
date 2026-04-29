package org.mpad.host_backend.service;

import org.springframework.stereotype.Service;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@Service
public class InputControllerService {
    private Robot robot;
    // The accumulator saves small scroll movements so slow scrolling works perfectly
    private float scrollAccumulator = 0f;

    public InputControllerService() {
        try {
            this.robot = new Robot();
            // Critical for high-speed responsiveness
            this.robot.setAutoDelay(0);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void moveCursor(float dx, float dy) {
        if (robot == null) return;

        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        if (pointerInfo == null) return;
        Point currentPos = pointerInfo.getLocation();

        // Use Math.round instead of (int) for smoother sub-pixel movement
        int targetX = Math.round(currentPos.x + dx);
        int targetY = Math.round(currentPos.y + dy);

        robot.mouseMove(targetX, targetY);
    }

    public void leftClick() {
        if (robot == null) return;
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void rightClick() {
        if (robot == null) return;
        try {
            // Reset state just in case
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);

            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);

            // ADD THIS: A tiny 10-20ms delay.
            // This ensures Windows "sees" the click and opens the context menu.
            robot.delay(15);

            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
        } catch (Exception e) {
            System.err.println("Right click error: " + e.getMessage());
        }
    }

    public void doubleClick() {
        if (robot == null) return;
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        robot.delay(70);

        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void scroll(float amount) {
        if (robot == null) return;

        // Add current movement to the "bank"
        scrollAccumulator += amount;

        // If we have enough for at least one full 'notch' of the wheel
        int notches = (int) scrollAccumulator;

        if (notches != 0) {
            robot.mouseWheel(notches);
            // Subtract only the whole notches we used, keep the fractional remainder
            scrollAccumulator -= notches;
            System.out.println("SCROLLING: " + notches + " notches");
        }
    }

    public void typeCharacter(String keyData) {
        if (robot == null || keyData == null || keyData.isEmpty()) return;

        try {
            if (keyData.equals("BACKSPACE")) {
                robot.keyPress(KeyEvent.VK_BACK_SPACE);
                robot.keyRelease(KeyEvent.VK_BACK_SPACE);
                return;
            }
            if (keyData.equals("ENTER")) {
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
                return;
            }

            // Handle standard characters
            char c = keyData.charAt(0);
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);

            if (keyCode != KeyEvent.VK_UNDEFINED) {
                boolean useShift = Character.isUpperCase(c) || isShiftRequired(c);

                if (useShift) robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
                if (useShift) robot.keyRelease(KeyEvent.VK_SHIFT);
            }
        } catch (Exception e) {
            System.err.println("Typing error: " + e.getMessage());
        }
    }

    // Helper method for common shifted symbols (like @, !, #, etc.)
    private boolean isShiftRequired(char c) {
        String shiftedChars = "~!@#$%^&*()_+{}|:\"<>?";
        return shiftedChars.indexOf(c) >= 0;
    }
}