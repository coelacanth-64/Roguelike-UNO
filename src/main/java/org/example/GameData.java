package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.*;

public class GameData {

    private static final long seed = System.nanoTime();
    private static final Random generator = new Random(seed);

    static ArrayList<Integer> deck = new ArrayList<>();
    static Stack<Integer> discardPile = new Stack<>();

    public static int displayCard(int cardID) {
        int id = 100;
        try{
            CSVReader reader = new CSVReaderBuilder(new FileReader("C:\\Users\\ashto\\Documents\\CS491\\uno3\\src\\main\\resources\\cardData.csv"))
                    .withSkipLines(cardID + 1).build();

            String[] nextline;
            nextline = reader.readNext();
            if(nextline != null) {
                id = Integer.parseInt(nextline[1]);
                System.out.print("id: " + nextline[1]); // index is column number
                System.out.print(" | value: " + nextline[2]); // index is column number
                System.out.print(" | color: "); // index is column number
                    if (Objects.equals(nextline[3], "1")) {
                        System.out.print("red");
                    }
                    if (Objects.equals(nextline[3], "2")) {
                        System.out.print("blue");
                }
                    if (Objects.equals(nextline[3], "3")) {
                        System.out.print("green");
                }
                    if (Objects.equals(nextline[3], "4")) {
                        System.out.print("yellow");
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
        System.out.println("\nFinished.");
        return(id);
    };

    public static void playCard() { // test
        System.out.println("playCardTest");

        System.out.println("\nIndex of card to play:");
        Player.displayHand(Player.hand);
        System.out.print("Current Card: ");
        displayCard(GameData.discardPile.peek());

        Scanner playCardScanner = new Scanner(System.in);
        Integer cardSelection = playCardScanner.nextInt();
        playCardScanner.nextLine();

        int selectionIndex = cardSelection - 1; // -1 so indexing starts at 1 for convenience

        // Draw card test
        if (cardSelection == 777) {
            Player.drawCard(1);
            System.out.println("Hand: " + Player.hand);
            playCard();
        }
        else if (Player.hand.size() < cardSelection) {
            System.out.println("Invalid card.");
            playCard();
        }

        int cardID = Player.hand.get(selectionIndex);



        if (GameData.testPlayable(cardID)) {
            // display chosen card, discard pile, and new hand
            // (remember, hand array is just card id, so only card id is selected & added)
            GameData.discardPile.add(Player.hand.get(selectionIndex)); // add to discard pile
            Player.hand.remove(selectionIndex); // remove from player hand

            Player.displayHand(Player.hand); // display new hand
            playCard();
        }
        else {
            System.out.println("Invalid card.");
            playCard();
        }
        return;
    }

    // Test card playability
    public static boolean testPlayable(int cardID) {

        String playColor = "", playValue= "", discardValue="", discardColor = "";
        int playColorInt, playValueInt, discardValueInt, discardColorInt;

        try {
            CSVReader playableReader = new CSVReaderBuilder(new FileReader("C:\\Users\\ashto\\Documents\\CS491\\uno3\\src\\main\\resources\\cardData.csv"))
                    .withSkipLines(cardID + 1).build();

            CSVReader discardReader = new CSVReaderBuilder(new FileReader("C:\\Users\\ashto\\Documents\\CS491\\uno3\\src\\main\\resources\\cardData.csv"))
                    .withSkipLines(GameData.discardPile.peek() + 1).build();

            System.out.println(GameData.discardPile.peek());

            String[] nextlinePlay;
            nextlinePlay = playableReader.readNext();
            if (nextlinePlay != null) {
                playColor = (nextlinePlay[3]);
                playValue = (nextlinePlay[2]); // get value
            }

            String[] nextlineDiscard;
            nextlineDiscard = discardReader.readNext();
            if (nextlineDiscard != null) {
                discardColor = (nextlineDiscard[3]); // get color
                discardValue = (nextlineDiscard[2]); // get value
            }

        }catch (Exception e){
            System.out.println(e);
        }

        playColorInt = Integer.parseInt(playColor);
        playValueInt = Integer.parseInt(playValue);
        discardColorInt = Integer.parseInt(discardColor);
        discardValueInt = Integer.parseInt(discardValue);

        /*

        Testing for correct values

        System.out.print("After setting int value:\n"
                     + "playColorInt: " + playColorInt + " playColor: " + playColor
                     + "\nplayValueInt: " + playValueInt + " playValue: " + playValue
                     + "\ndiscardColorInt: " + discardColorInt + " discardColor: " + discardColor
                     + "\ndiscardValueInt: " + discardValueInt + " discardValue: " + discardValue + "\n");
         */

        if (playColorInt == discardColorInt || playValueInt == discardValueInt) {
            System.out.println("True");
            return(true);
        }
        else {
            System.out.println("False");
            return(false);
        }
    }


    // Initialize discard pile
    public static Stack<Integer> discardInit() {
        int randomIndex = generator.nextInt(deck.size()); // generate a random index from deck array
        int card = deck.remove(randomIndex); // remove a number from arraylist and set it as card
        discardPile.add(card); // add that index value to discard
        return discardPile;
    }

    // Initialize deck
    public static ArrayList<Integer> deckInit (int deckSize) {

        for(int i = 0; i <= deckSize; i++) {
            deck.add(i);
        }

        System.out.println(deck + "\ndeckInit finished.");
        return deck;
    }

/*
    public static void gameMenu() {
        System.out.println("Welcome to UNO!\nChoose an option:\n1. Play\n2.Settings (WIP)\n3.Close game.\n");
        Scanner scanner = new Scanner(System.in);

        Integer select = scanner.nextInt();
        switch (select) {
            default:
                System.out.println("Please select a valid option.");
                gameMenu();
            case 1:
                startGame();
                break;
            case 2:
                Settings();
                break;
            case 3:
                System.out.println("Goodbye!");
                System.exit(0);
                break;
        }
    }
*/

/*
    public static void startGame() {
        System.out.println("Started.");
    }

    public static void Settings() {
        System.out.println("Settings.");
    }
*/

    public static void main(String[] args) {
    }

}
