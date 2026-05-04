package org.example;

import javax.swing.JLabel;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.Beans;   

public class ScaledImageLabel extends JLabel {

    private Image image;

    public ScaledImageLabel() {
        super();
        setOpaque(false);
    }

    public void setImage(Image img) {
        this.image = img;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}