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
    static boolean drawFour = false;
    static boolean drawTwo = false;
    static boolean skip = false;
    static boolean reverse = false;
    static boolean debug = false;
    static boolean gameEnabled = true;
    static int turnDirection = 1;

    public static int getCardInfo(int id, int index) {
        int value = 0;

        try{
            CSVReader reader = new CSVReaderBuilder(new FileReader("src/main/resources/cardData.csv"))
                    .withSkipLines(id + 1).build();
            String[] nextline;
            nextline = reader.readNext();

            if(nextline != null) {
                value = Integer.parseInt(nextline[index]);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return value;
    }

    public static int displayCard(int cardID) {
        int id = 100;
        try{
            CSVReader reader = new CSVReaderBuilder(new FileReader("src/main/resources/cardData.csv"))
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

    public static void playCard(Player player) { // test
        if (debug) System.out.println("playCardTest");
        System.out.println("Index of card to play:");
        player.displayHand(player.hand);
        System.out.print("Current Card: ");
        displayCard(GameData.discardPile.peek());

        Scanner playCardScanner = new Scanner(System.in);
        Integer cardSelection = playCardScanner.nextInt();
        playCardScanner.nextLine();

        int selectionIndex = cardSelection - 1; // -1 so indexing starts at 1 for convenience

        // Draw card
        if (cardSelection == 777) {
            player.drawCard(1);
            System.out.println("Hand: " + player.hand);
            playCard(player);
            return;
        }
        // OOB card detection
        else if (player.hand.size() < cardSelection) {
            System.out.println("Invalid card.");
            playCard(player);
            return;
        }

        int cardID = player.hand.get(selectionIndex);

        if (GameData.testPlayable(cardID)) {

            if (getCardInfo(cardID, 2) == 10 || getCardInfo(cardID, 2) == 11 || getCardInfo(cardID, 2) == 12 || getCardInfo(cardID, 2) == 13) {
                switch (getCardInfo(cardID, 2)) {
                    default:
                        System.out.println("Invalid action card");
                        break;
                    case 10:
                        System.out.println("+2");
                        drawTwo = true;
                        break;
                    case 11:
                        System.out.println("+4");
                        drawFour = true;
                        break;
                    case 12:
                        System.out.println("Skip");
                        skip = true;
                        break;
                    case 13:
                        System.out.println("Reverse");
                        reverse = !reverse;
                        turnDirection *= -1;
                        break;

                }
            }
            // display chosen card, discard pile, and new hand
            // (remember, hand array is just card id, so only card id is selected & added)
            GameData.discardPile.add(player.hand.get(selectionIndex)); // add to discard pile
            player.hand.remove(selectionIndex); // remove from player hand
        }
        else {
            System.out.println("Invalid card.");
            playCard(player);
            return;
        }
    }

    public static boolean testPlayable(int cardID) {

        String playColor = "", playValue= "", discardValue="", discardColor = "";
        int playColorInt, playValueInt, discardValueInt, discardColorInt;

        try {
            CSVReader playableReader = new CSVReaderBuilder(new FileReader("src/main/resources/cardData.csv"))
                    .withSkipLines(cardID + 1).build();

            CSVReader discardReader = new CSVReaderBuilder(new FileReader("src/main/resources/cardData.csv"))
                    .withSkipLines(GameData.discardPile.peek() + 1).build();

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

        } catch (Exception e) {
            System.out.println(e);
        }

        playColorInt = Integer.parseInt(playColor);
        playValueInt = Integer.parseInt(playValue);
        discardColorInt = Integer.parseInt(discardColor);
        discardValueInt = Integer.parseInt(discardValue);

        if (playColorInt == discardColorInt || playValueInt == discardValueInt) {
            if (debug) System.out.println("True; testPlayable Finished.");
            return(true);
        }
        else {
            if (debug) System.out.println("False; testPlayable Finished.");
            return(false);
        }
    }

    public static Stack<Integer> discardInit() {
        int randomIndex = generator.nextInt(deck.size()); // generate a random index from deck array
        int card = deck.remove(randomIndex); // remove a number from arraylist and set it as card
        discardPile.add(card); // add that index value to discard
        if (debug) System.out.println("discardInit Finished" + discardPile);
        return discardPile;
    }

    public static ArrayList<Integer> deckInit (int deckSize) {

        for(int i = 0; i <= deckSize; i++) {
            deck.add(i);
        }

        if (debug) System.out.println("deckInit finished." + deck);
        return deck;
    }

    public static void initGame(int handSize, int deckSize) {
        deckInit(deckSize);

        Player player1 = new Player();
        player1.handInit(handSize, deck);
        player1.turnValue = 0;

        Player player2 = new Player();
        player2.handInit(handSize, deck);
        player2.turnValue = 1;

        Player player3 = new Player();
        player3.handInit(handSize, deck);
        player3.turnValue = 2;

        Player player4 = new Player();
        player4.handInit(handSize, deck);
        player4.turnValue = 3;

        discardInit();

        int turnCount = 0;

        // while true, the game runs
        while (gameEnabled) {
            turnValue = Math.floorMod(turnCount, 4);
            if (debug) System.out.println("turnCount: " + turnCount + " | turnValue: " + turnValue);

            if (turnValue == player1.turnValue) {
                System.out.println("Player 1 turn:");

                if (drawFour) {
                    player1.drawCard(4);
                    drawFour = false;
                    playCard(player1);
                }
                if (drawTwo) {
                    player1.drawCard(2);
                    drawTwo = false;
                    playCard(player1);
                }
                if (skip) {
                    skip = false;
                } else playCard(player1);
            }
            else if (turnValue == player2.turnValue) {
                System.out.println("Player 2 turn:");

                if (drawFour) {
                    player2.drawCard(4);
                    drawFour = false;
                    playCard(player2);
                }
                if (drawTwo) {
                    player2.drawCard(2);
                    drawTwo = false;
                    playCard(player2);
                }
                if (skip) {
                    skip = false;
                } else playCard(player2);
            }
            else if (turnValue == player3.turnValue) {
                System.out.println("Player 3 turn:");

                if (drawFour) {
                    player3.drawCard(4);
                    drawFour = false;
                    playCard(player3);
                }
                if (drawTwo) {
                    player3.drawCard(2);
                    drawTwo = false;
                    playCard(player3);
                }
                if (skip) {
                    skip = false;
                } else playCard(player3);
            }
            else if (turnValue == player4.turnValue) {
                System.out.println("Player 4 turn:");

                if (drawFour) {
                    player4.drawCard(4);
                    drawFour = false;
                    playCard(player4);
                }
                if (drawTwo) {
                    player4.drawCard(2);
                    drawTwo = false;
                    playCard(player4);
                }
                if (skip) {
                    skip = false;
                } else playCard(player4);
            }
            turnCount += turnDirection;

            if (player1.hand.isEmpty() || player2.hand.isEmpty() || player3.hand.isEmpty() ||player4.hand.isEmpty()) {
                System.out.println("Winner decided! Congratulations!") ;
                gameEnabled = false;
            }
        }
    }

    static int turnValue;

    public static void main(String[] args) {
        initGame(1, 52);
    }
}
