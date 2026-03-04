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
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	ColorFillUI ui = new ColorFillUI();
        	GameController controller = new GameController(ui);
        });
    }
}