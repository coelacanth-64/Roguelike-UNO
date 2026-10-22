package org.example;

import javax.swing.*;
import java.awt.*;

public class gameGUI extends JPanel implements Runnable {
    private Thread gameThread;
    private boolean running = false;
    private final int FPS = 60;
    

    public gameGUI() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); // Ensures smooth rendering
    }

    public void startGame() {
        gameThread = new Thread(this);
        running = true;
        gameThread.start();
    }

    @Override
    public void run() {
        // Calculate interval in nanoseconds (1 second / FPS)
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (running) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                // 1. Update positions/game state
                updateLogic();

                // 2. Repaint the screen (requests paintComponent)
                repaint();

                delta--;
            }
        }
    }

    private void updateLogic() {
        // Update your game objects, physics, and positions here
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw your game objects here using g2

        g2.dispose();
    }
}
