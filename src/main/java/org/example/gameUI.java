package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;

import com.opencsv.*;

public class gameUI extends JPanel {

    String name;
    String id;
    String value;
    String color;
    String description;
    String imagePath;


    private CardData data;
    private Image baseCardImage;
    private Image cardIcon;

    public gameUI(CardData data) {
        this.data = data;


        // Load base card background (replace with your actual path)
        baseCardImage = new ImageIcon("src/main/resources/cardface1.png").getImage();

        // Load card-specific image from CSV
        cardIcon = new ImageIcon(getClass().getResource("/" + data.imagePath)).getImage();

        setPreferredSize(new Dimension(300, 450));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw card icon
        g2.drawImage(cardIcon, 35, 65, 240, 200, this);

        // Draw base card
        g2.drawImage(baseCardImage, 0, 0, getWidth(), getHeight(), this);

        // Draw text
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString(data.name, 120, 50);

        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        //g2.drawString("ID: " + data.id, 120, 80);
        g2.drawString(data.color, 20, 40);

        g2.setFont(new Font("Arial", Font.ITALIC, 36));
        drawMultiline(g2, data.description, 40, 300);
    }

    private void drawMultiline(Graphics2D g2, String text, int x, int y) {
        for (String line : text.split("\n")) {
            g2.drawString(line, x, y);
            y += g2.getFontMetrics().getHeight();
        }
    }

    public static void main(String[] args) {

        CardData card = new CardData(53);   // loads CSV row #5

        JFrame frame = new JFrame("Card Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new gameUI(card));
        frame.pack();
        frame.setVisible(true);
    }
}