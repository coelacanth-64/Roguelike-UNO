package org.example;

import java.util.List;
import java.util.Scanner;

/*
    The purpose of this class is for testing the UNO game without implementing the necessary UI.
    Any interface with the Console should be done through this class, not GameData
*/

public class Console implements ColorChooser {
    private final GameData GameData;
    private final Scanner scanner = new Scanner(System.in);

    public Console(GameData GameData) { this.GameData = GameData; }

    @Override
    public int chooseColor(int playerIndex, int cardId) { // alternate chooseColor for the Console interface
        while (true) {
            System.out.println("Choose color: 1=Red 2=Blue 3=Green 4=Yellow");
            if (!scanner.hasNextInt()) { scanner.next(); continue; }
            int select = scanner.nextInt();
            if (select >= 1 && select <= 4) return select;
            System.out.println("Invalid selection.");
        }
    }

    public void runConsoleGame(int handSize, int deckSize, int numPlayers) {
        GameData.initGame(handSize, deckSize, numPlayers); // initialize game
        int turnCount = 0;

        // game loop
        while (GameData.isGameEnabled()) {
            int currentPlayer = GameData.turnIndex(turnCount);

            if (Main.debug) {
                System.out.println("DEBUG (PRE ACTION CARD): Player " + (currentPlayer + 1) + " hand:");
                GameData.getPlayers().get(currentPlayer).displayHandToConsole(GameData);
                System.out.println("DEBUG (DECK): " + GameData.getDeck());
                System.out.println("DEBUG (DECK SIZE): " + GameData.getDeckSize());
            }

            if (GameData.drawTwo)
                GameData.drawCards(currentPlayer, 2);
            else if (GameData.drawFour)
                GameData.drawCards(currentPlayer, 4);
            else if (GameData.skip) {
                System.out.println("Skipped player " + (currentPlayer + 1) + "'s turn.");
                GameData.skip = false;
                turnCount += GameData.turnDirection;
                currentPlayer = GameData.turnIndex(turnCount);
            }

            if (Main.debug) {
                System.out.println("DEBUG (POST ACTION CARD): Player " + (currentPlayer + 1) + " hand:");
                GameData.getPlayers().get(currentPlayer).displayHandToConsole(GameData);
            }

            if (!GameData.checkHandPlayable(currentPlayer)) {
            System.out.println("Unplayable hand. Drawing until playable.");
            while (!GameData.checkHandPlayable(currentPlayer)) {
                GameData.drawCards(currentPlayer, 1);
                if (Main.debug) {
                    System.out.println("DEBUG (PLAYABLEHANDCHECK): Player " + (currentPlayer + 1) + " hand:");
                    GameData.getPlayers().get(currentPlayer).displayHandToConsole(GameData);
                }
            }
        }

            if (Main.debug) System.out.println("DEBUG: POST PLAYABLE HAND CHECK\nReverse State: " + GameData.reverse);
            System.out.println("Player " + (currentPlayer + 1) + " hand:");
            GameData.getPlayers().get(currentPlayer).displayHandToConsole(GameData);
            System.out.println("------------------------------------\nTop card: " + GameData.getCardData(GameData.getTopDiscard()));

            System.out.print("Type index of card to play (1 - " + GameData.getPlayers().get(currentPlayer).hand.size() + ")");
            if (Main.debug) System.out.print(" or 777 to draw: ");
            if (!scanner.hasNextInt()) { scanner.next(); continue; }
            int selection = scanner.nextInt();

            // manual draw card
            if (selection == 777 && Main.debug) {
                List<Integer> drawn = GameData.drawCards(currentPlayer, 1);
                System.out.println("Drew: " + drawn);
            } else {
                PlayResult result = GameData.playCard(currentPlayer, selection - 1, this);
                if (!result.winner) System.out.println("Invalid play: " + result.message);
                    else System.out.println("Played card id: " + result.playedCardID);
                if (result.playerWon) { System.out.println("Winner decided! Congratulations!"); break; }
            }
            turnCount += GameData.turnDirection;
        }
    }
}