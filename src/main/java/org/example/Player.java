package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/*
    This class, similar to the CardData class, allows for storing of all of a player's information in one place
    Additionally, all interactions with player properties are handled here
*/

public class Player {
    int turnValue;
    ArrayList<Integer> hand = new ArrayList<>();

    private static final long seed = System.nanoTime();
    private static final Random generator = new Random(seed);

    public void drawCard(int drawAmount, ArrayList<Integer> deck) {
        int cardID = 0;

        for (int i = 0; i < drawAmount && !deck.isEmpty(); i++) {
            int randomIndex = generator.nextInt(deck.size()); // generate a random index from deck array
            int card = deck.remove(randomIndex); // remove a number from arraylist and set it as card
            hand.add(card); // add that index value to hand
        }
    }

    public ArrayList<Integer> handInit(int handSize, ArrayList<Integer> deck) {
        hand.clear();

        if (Main.debug) {
            System.out.println("handInit called: deck identity=" + System.identityHashCode(deck) + " deck size=" + deck.size());
        }

        for (int i = 0; i < handSize && !deck.isEmpty(); i++) {
            int randomIndex = generator.nextInt(deck.size()); // generate a random index from deck array
            Integer card = deck.remove(randomIndex); // remove a number from arraylist and set it as card
            if (card == null) {
                System.err.println("handInit: skipped null card from deck at index " + randomIndex);
                i--; // try again to reach desired hand size
                continue;
            }

            hand.add(card); // add that index value to hand
        }
        if (Main.debug) System.out.println(hand + "\nhandInit finished.");
        return new ArrayList<>(hand);
    }

    public void displayHandToConsole(GameData game) {
        for (int i = 0; i < hand.size(); i++) {
            int cardID = hand.get(i);
            CardData cd = game.getCardData(cardID);
            System.out.println((i + 1) + ": " + cd.toString());
        }
    }
}
