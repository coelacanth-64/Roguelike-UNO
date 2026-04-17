package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Player {

    static ArrayList<Integer> hand = new ArrayList<>();
    private static final long seed = System.nanoTime();
    private static final Random generator = new Random(seed);

    public static int drawCard(int drawAmount) {
        int cardID = 0;

        for (int i = 0; i < drawAmount && !GameData.deck.isEmpty(); i++) {
            int randomIndex = generator.nextInt(GameData.deck.size()); // generate a random index from deck array
            int card = GameData.deck.remove(randomIndex); // remove a number from arraylist and set it as card
            hand.add(card); // add that index value to hand
        }

        return cardID;
    }

    // Displays player hand
    public static void displayHand(ArrayList<Integer> hand) {
        int cardID = 0;

        for(int i = 0; i < hand.size(); i++) {
            System.out.print(i + 1 + ": ");
            cardID = hand.get(i);

            try {
                CSVReader reader = new CSVReaderBuilder(new FileReader("C:\\Users\\ashto\\Documents\\CS491\\uno3\\src\\main\\resources\\cardData.csv"))
                        .withSkipLines(cardID + 1).build();

                String[] nextline;
                nextline = reader.readNext();
                if (nextline != null) {
                    System.out.print(Arrays.toString(nextline) + " ");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println("");
        }
    };

    // Initialize starting hand with given hand size and deck (interchangable)
    public static ArrayList<Integer> drawStartHand(int handSize, ArrayList<Integer> deck) {
        for (int i = 0; i < handSize && !deck.isEmpty(); i++) {
            int randomIndex = generator.nextInt(deck.size()); // generate a random index from deck array
            int card = deck.remove(randomIndex); // remove a number from arraylist and set it as card
            hand.add(card); // add that index value to hand
        }

        System.out.println(hand + "\ndrawStartHand finished.");
        return new ArrayList<>(hand);
    }

    public static void main(String[] args) {

    }
}
