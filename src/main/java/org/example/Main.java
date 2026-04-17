package org.example;

import java.util.*;

public class Main {

    private static final long seed = System.nanoTime();
    private static final Random generator = new Random(seed);

    public static void Select() {
        System.out.println("Select an option:\n1. displayHand\n2. playCardTest\n3.");

        Scanner selectScanner = new Scanner(System.in);
        Integer select = selectScanner.nextInt();

        switch (select) {
            default:
                System.out.println("Please select a valid option.");
                Select();
            case 1:
                Player.displayHand(Player.drawStartHand(7, GameData.deckInit(52)));
                break;
            case 2:
                GameData.playCard();
                break;
        }

        return;
    }

        public static void main (String[]args){
            Player.drawStartHand(7, GameData.deckInit(52)); // draw start hand
            GameData.discardInit();

            Select();
        }
    }