/*
 * HelloSwing.java
 * Entry point for the Color Fill Game application.
 * Initializes the UI and game controller in the Swing event dispatch thread.
 * Author: Bhargava Srisai
 * Version: 3.0
 */

package swingprac;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class HelloSwing {
    /**
     * Main entry point for the Color Fill Game application.
     * Launches the UI in the Swing event dispatch thread to ensure thread safety.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	ColorFillUI ui = new ColorFillUI();
        	GameController controller = new GameController(ui);
        });
    }
}