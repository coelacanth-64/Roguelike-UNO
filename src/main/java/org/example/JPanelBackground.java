package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class JPanelBackground extends JPanel {
    private BufferedImage backgroundImage;

    // REQUIRED by NetBeans GUI Builder
    public JPanelBackground() {
        try {
            var stream = getClass().getResourceAsStream("/bg.jpg");
            if (stream != null) {
                backgroundImage = ImageIO.read(stream);
            }
        } catch (Exception e) {
            // swallow exceptions so NetBeans doesn't reject the class
        }
        setOpaque(true);
    }

    // Optional: keep your custom setter
    public void setBackgroundImage(BufferedImage img) {
        this.backgroundImage = img;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}