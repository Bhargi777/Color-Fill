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