/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.example;

public class Bot extends Player implements ColorChooser {

    public int chooseCardIndex(GameData game) {
        int bestIndex = -1;
        int bestScore = Integer.MIN_VALUE;

        // Count the number of cards of each color.
        int[] colorCounts = new int[5];

        for (int cardID : hand) {
            int color = game.getCardInfo(cardID, 3);

            if (color >= 1 && color <= 4) {
                colorCounts[color]++;
            }
        }

        // Find the best playable card.
        for (int i = 0; i < hand.size(); i++) {
            int cardID = hand.get(i);

            if (!game.testPlayable(cardID)) {
                continue;
            }

            int value = game.getCardInfo(cardID, 2);
            int color = game.getCardInfo(cardID, 3);

            int score = 0;

            // Prefer colors that appear frequently in the hand.
            if (color >= 1 && color <= 4) {
                score += colorCounts[color] * 10;
            }

            // Slightly prioritize action cards.
            if (value >= 10 && value <= 13) {
                score += 3;
            }

            // Save Wild cards if a normal card is playable.
            if (value == 51 || value == 52) {
                score -= 100;
            }

            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
            }
        }

        return bestIndex;
    }

    @Override
    public int chooseColor(int playerIndex, int cardId) {
        int[] colorCounts = new int[5];

        // Count the remaining colored cards.
        for (int id : hand) {
            int color = getColor(id);

            if (color >= 1 && color <= 4) {
                colorCounts[color]++;
            }
        }

        // Choose the most common color.
        int bestColor = 1;

        for (int color = 2; color <= 4; color++) {
            if (colorCounts[color] > colorCounts[bestColor]) {
                bestColor = color;
            }
        }

        return bestColor;
    }

    private int getColor(int cardID) {
        return new CardData(cardID).colorID;
    }
}