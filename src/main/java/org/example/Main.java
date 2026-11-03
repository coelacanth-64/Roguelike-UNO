package org.example;

import java.util.*;
import javax.swing.*;

/*
    Main should primarily be used to initialize the game itself, as well as passing specific
    arguments we may need for testing (ex. debug allows to see more console outputs to know what is being executed)
*/

public class Main {
    public static boolean debug = false;
    public static int handSize = 7;
    public static int deckSize = 54;
    public static int playerCount = 4;
    public static boolean runUIGame = true;
    public static boolean runConsoleGame = false;

    public static void main(String[] args) {

        if (runUIGame) {
            System.out.println("Running game UI.");
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Roguelike UNO");
                gameGUI gui = new gameGUI();

                frame.setContentPane(gui);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                gui.startGame();

            });
        }

        // not fully finished, using older code but should still work.
        if (runConsoleGame) {
            System.out.println("Running game in console.");
            GameData game = new GameData();
            Console console = new Console(game);

            console.runConsoleGame(handSize, deckSize, playerCount);
        }
    }
}