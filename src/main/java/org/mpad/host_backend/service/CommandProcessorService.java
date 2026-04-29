package org.mpad.host_backend.service;

import org.mpad.host_backend.model.CommandPayload;
import org.springframework.stereotype.Service;

@Service
public class CommandProcessorService {
    private final InputControllerService inputController;

    public CommandProcessorService(InputControllerService inputController) {
        this.inputController = inputController;
    }

    public void process(CommandPayload command) {
        if (command == null || command.getType() == null) return;

        // Log incoming commands for debugging
        System.out.println("Processing: " + command.getType());

        switch (command.getType()) {
            case "MOUSE_MOVE":
                inputController.moveCursor(command.getX(), command.getY());
                break;
            case "LEFT_CLICK":
                inputController.leftClick();
                break;
            case "RIGHT_CLICK":
                inputController.rightClick();
                break;
            case "DOUBLE_CLICK": // Added to support Tap-to-Click double taps
                inputController.doubleClick();
                break;
            case "SCROLL":
                // Using getY for vertical scroll distance
                inputController.scroll(command.getY());
                break;
            case "KEY_TYPE":
                inputController.typeCharacter(command.getData());
                break;
            default:
                System.out.println("Unknown command received: " + command.getType());
        }
    }
}