package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

public class gameGUI extends JPanel implements Runnable, ColorChooser {
    private Thread gameThread;
    private boolean running = false;
    private final int FPS = 60;

    private final GameData gameData;
    private final JPanel handPanel;
    private final JLabel statusLabel;
    private int turnCount = 0;
    private int currentPlayer = 0;

    public gameGUI() {
        this.gameData = new GameData();
        this.gameData.initGame(Main.handSize, Main.deckSize, Main.playerCount);

        this.setPreferredSize(new Dimension(1000, 700));
        this.setBackground(new Color(25, 50, 30));
        this.setDoubleBuffered(true);
        this.setLayout(new BorderLayout(10, 10));

        statusLabel = new JLabel("Player " + (currentPlayer + 1) + "'s turn", SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        this.add(statusLabel, BorderLayout.NORTH);

        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        handPanel.setOpaque(false);
        this.add(handPanel, BorderLayout.CENTER);

        refreshHand();
    }

    public void startGame() {
        gameThread = new Thread(this);
        running = true;
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (running) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                updateLogic();
                repaint();
                delta--;
            }
        }
    }

    private void updateLogic() {
        // Turns are driven by the player UI actions, not by this render loop.
    }

    private void refreshHand() {
        handPanel.removeAll();

        if (gameData.getPlayers().isEmpty()) {
            JLabel emptyLabel = new JLabel("No players in game.");
            emptyLabel.setForeground(Color.WHITE);
            handPanel.add(emptyLabel);
            statusLabel.setText("No players");
            handPanel.revalidate();
            handPanel.repaint();
            return;
        }

        List<Integer> currentHand = gameData.getPlayerHand(currentPlayer);

        for (int i = 0; i < currentHand.size(); i++) {
            int cardID = currentHand.get(i);
            CardData cardData = gameData.getCardData(cardID);

            JButton cardButton = new JButton(loadCardIcon(cardData.imagePath));
            cardButton.setPreferredSize(new Dimension(110, 150));
            cardButton.setBorder(BorderFactory.createEmptyBorder());
            cardButton.setFocusPainted(false);
            cardButton.setContentAreaFilled(false);
            cardButton.setOpaque(false);
            cardButton.setToolTipText(cardData.toString());

            if (!gameData.testPlayable(cardID)) {
                cardButton.setEnabled(false);
                cardButton.setToolTipText(cardData.toString() + " (not playable)");
            }

            final int cardIndex = i;
            cardButton.addActionListener(e -> handleCardClick(cardIndex));
            handPanel.add(cardButton);
        }

        statusLabel.setText("Player " + (currentPlayer + 1) + "'s turn");
        handPanel.revalidate();
        handPanel.repaint();
    }

    private void handleCardClick(int cardIndex) {
        if (!gameData.isGameEnabled()) {
            JOptionPane.showMessageDialog(this, "The game is over.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        PlayResult result = gameData.playCard(currentPlayer, cardIndex, this);
        if (!result.winner) {
            JOptionPane.showMessageDialog(this, result.message, "Invalid Play", JOptionPane.WARNING_MESSAGE);
            refreshHand();
            return;
        }

        if (result.playerWon) {
            statusLabel.setText("Player " + (currentPlayer + 1) + " wins!");
            handPanel.removeAll();
            JLabel winnerLabel = new JLabel("Winner: Player " + (currentPlayer + 1));
            winnerLabel.setForeground(Color.WHITE);
            winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            handPanel.add(winnerLabel);
            handPanel.revalidate();
            handPanel.repaint();
            return;
        }

        if (gameData.drawTwo) {
            gameData.drawCards(currentPlayer, 2);
            gameData.drawTwo = false;
        } else if (gameData.drawFour) {
            gameData.drawCards(currentPlayer, 4);
            gameData.drawFour = false;
        }

        if (gameData.skip) {
            gameData.skip = false;
            turnCount += gameData.turnDirection;
            currentPlayer = gameData.turnIndex(turnCount);
            turnCount += gameData.turnDirection;
            currentPlayer = gameData.turnIndex(turnCount);
            refreshHand();
            return;
        }

        turnCount += gameData.turnDirection;
        currentPlayer = gameData.turnIndex(turnCount);
        refreshHand();
    }

    private ImageIcon loadCardIcon(String imagePath) {
        String resourcePath = (imagePath == null || imagePath.isBlank()) ? "horse.png" : imagePath;
        URL imageURL = getClass().getClassLoader().getResource(resourcePath);

        if (imageURL == null) {
            Image placeholder = new BufferedImage(110, 150, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = placeholder.createGraphics();
            g2.setColor(new Color(32, 32, 32));
            g2.fillRect(0, 0, 110, 150);
            g2.setColor(Color.WHITE);
            g2.drawRect(5, 5, 100, 140);
            g2.dispose();
            return new ImageIcon(placeholder);
        }

        ImageIcon icon = new ImageIcon(imageURL);
        Image scaled = icon.getImage().getScaledInstance(110, 150, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    @Override
    public int chooseColor(int playerIndex, int cardId) {
        String[] colorNames = {"Red", "Blue", "Green", "Yellow"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose the new color for the wild card.",
                "Choose Color",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                colorNames,
                colorNames[0]
        );

        if (choice < 0) {
            return 1;
        }
        return choice + 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(new Color(25, 50, 30));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose();
    }
}
