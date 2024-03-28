package src.Client;

import javax.swing.JButton;
import java.awt.*;

/**
 * Class that helps customize buttons
 * 
 * @author Anthony Arseneau
 * @version March 28, 2024
 * Networks project
 */
class MyButton extends JButton {
    // Instance variables
    private Color hoverBackgroundColor;
    private Color pressedBackgroundColor;

    // Constructor
    public MyButton() {
        this(null);
    }

    /**
     * Constructor
     * @param text the text inside the button
     */
    public MyButton(String text) {
        super(text);
        super.setContentAreaFilled(false);
    }

    /**
     * Method to customize the style of the button
     */
    @Override
    protected void paintComponent(Graphics g) {
        // When button is pressed
        if (getModel().isPressed()) {
            g.setColor(pressedBackgroundColor);
        // When cursor hovers the button
        } else if (getModel().isRollover()) {
            g.setColor(hoverBackgroundColor);
            this.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Get the hand
        // Normal style
        } else {
            g.setColor(getBackground());
        }
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    /* Helper methods */
    @Override
    public void setContentAreaFilled(boolean b) {
    }

    public Color getHoverBackgroundColor() {
        return hoverBackgroundColor;
    }

    public void setHoverBackgroundColor(Color hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }

    public Color getPressedBackgroundColor() {
        return pressedBackgroundColor;
    }

    public void setPressedBackgroundColor(Color pressedBackgroundColor) {
        this.pressedBackgroundColor = pressedBackgroundColor;
    }
}