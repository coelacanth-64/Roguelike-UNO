package org.example;

import java.util.List;
import java.util.Scanner;

public class Console implements ColorChooser {
    private final GameData GameData;
    private final Scanner scanner = new Scanner(System.in);

    public Console(GameData GameData) { this.GameData = GameData; }

    @Override
    public int chooseColor(int playerIndex, int cardId) {
        while (true) {
            System.out.println("Choose color: 1=Red 2=Blue 3=Green 4=Yellow");
            if (!scanner.hasNextInt()) { scanner.next(); continue; }
            int sel = scanner.nextInt();
            if (sel >= 1 && sel <= 4) return sel;
            System.out.println("Invalid selection.");
        }
    }

    public void runConsoleGame(int handSize, int deckSize, int numPlayers) {
        GameData.initGame(handSize, deckSize, numPlayers); // initialize game
        int turnCount = 0;

        // game loop
        while (GameData.isGameEnabled()) {
            int currentPlayer = GameData.turnIndex(turnCount);
            if (!GameData.checkHandPlayable(currentPlayer)) {
                System.out.println("Unplayable hand. Drawing until playable.");
                while (!GameData.checkHandPlayable(currentPlayer)) {
                    GameData.drawCards(currentPlayer, 1);
                }
            }

            System.out.println("Player " + (currentPlayer + 1) + " hand:");
            GameData.getPlayers().get(currentPlayer).displayHandToConsole(GameData);

            System.out.print("Type index of card to play (1 - " + GameData.getPlayers().get(currentPlayer).hand.size() + ") or 777 to draw: ");
            if (!scanner.hasNextInt()) { scanner.next(); continue; }
            int selection = scanner.nextInt();

            // manual draw card
            if (selection == 777) {
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