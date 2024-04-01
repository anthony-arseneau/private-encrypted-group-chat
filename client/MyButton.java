package client;

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

    /**
     * Getter method to get the background color
     * @return the background color
     */
    public Color getHoverBackgroundColor() {
        return hoverBackgroundColor;
    }

    /**
     * Method to set the background color
     * @param hoverBackgroundColor the color of the background to set
     */
    public void setHoverBackgroundColor(Color hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }

    /**
     * Getter method to get the color of the background when pressed
     * @return
     */
    public Color getPressedBackgroundColor() {
        return pressedBackgroundColor;
    }

    /**
     * Method to set the color of the background when set
     * @param pressedBackgroundColor
     */
    public void setPressedBackgroundColor(Color pressedBackgroundColor) {
        this.pressedBackgroundColor = pressedBackgroundColor;
    }
}