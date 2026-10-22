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
    private JLabel statusLabel;
    private int turnCount = 0;
    private int currentPlayer = 0;
    private final JLabel discardLabel;
    private Image backgroundImage;

    public gameGUI() {
        this.gameData = new GameData();
        this.gameData.initGame(Main.handSize, Main.deckSize, Main.playerCount);

        backgroundImage = loadBackgroundImage("bg.png");

        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(1280, 720));

        JLabel statusLabel = new JLabel("Player " + (currentPlayer + 1) + "'s turn", SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(statusLabel, BorderLayout.NORTH);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setOpaque(false);

        JPanel discardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 150));
        discardPanel.setOpaque(false);

        discardLabel = new JLabel();
        discardLabel.setForeground(Color.WHITE);
        discardPanel.add(discardLabel);

        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        handPanel.setOpaque(false);
        handPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        root.add(discardPanel, BorderLayout.NORTH);
        root.add(handPanel, BorderLayout.SOUTH);

        add(root, BorderLayout.CENTER);

        refreshHand();
        updateDiscardDisplay();
    }

    private Image loadBackgroundImage(String fileName) {
        try {
            return new ImageIcon(getClass().getClassLoader().getResource("bg.png")).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateDiscardDisplay() {
        int topCardId = gameData.getTopDiscard();

        if (topCardId < 0) {
            discardLabel.setIcon(null);
            discardLabel.setText("No card yet");
            return;
        }

        CardData topCard = gameData.getCardData(topCardId);
        ImageIcon icon = loadCardIcon(topCard.imagePath);

        discardLabel.setText("");
        discardLabel.setIcon(icon);
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

        List<Integer> currentHand = gameData.getPlayerHand(currentPlayer);

        for (int i = 0; i < currentHand.size(); i++) {
            int cardID = currentHand.get(i);
            CardData card = gameData.getCardData(cardID);

            JButton cardButton = new JButton(loadCardIcon(card.imagePath));
            double scale = Math.max(0.45, 1.0 - (currentHand.size() - 7) * 0.08);
            int cardW = (int) (110 * scale);
            int cardH = (int) (150 * scale);
            cardButton.setPreferredSize(new Dimension(cardW, cardH));
            cardButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            cardButton.setFocusPainted(false);
            cardButton.setContentAreaFilled(false);
            cardButton.setBorderPainted(false);
            cardButton.setOpaque(false);
            cardButton.setBackground(new Color(255, 255, 255));
            cardButton.setToolTipText(card.toString());

            final int cardIndex = i;
            cardButton.addActionListener(e -> handleCardClick(cardIndex));

            handPanel.add(cardButton);
        }

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

        updateDiscardDisplay();

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
        String resourcePath = (imagePath == null || imagePath.isBlank()) ? "b2fv.png" : imagePath;
        URL imageURL = getClass().getClassLoader().getResource(resourcePath);

        if (imageURL == null) {
            BufferedImage placeholder = new BufferedImage(110, 150, BufferedImage.TYPE_INT_ARGB);
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

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

    }
}

