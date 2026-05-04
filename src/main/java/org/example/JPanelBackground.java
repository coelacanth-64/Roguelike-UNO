package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.beans.Beans;

public class JPanelBackground extends JPanel {

    private BufferedImage backgroundImage;

    public JPanelBackground() {

        // If NetBeans GUI Builder is rendering the form:
        if (Beans.isDesignTime()) {
            setBackground(Color.LIGHT_GRAY); // placeholder
            setOpaque(true);
            return;
        }

        try {
            var stream = getClass().getResourceAsStream("/placeholder-bg.jpg");
            if (stream != null) {
                backgroundImage = ImageIO.read(stream);
            }
        } catch (Exception ignored) {
        }

        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // At design time, just paint the placeholder
        if (Beans.isDesignTime()) {
            return;
        }

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
