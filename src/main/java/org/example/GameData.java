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
    static boolean gameEnabled = true;

    static int colorid = 5;
    static String color;
    static int value = 50;
    static int cid = 54; // card id

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
        try{
            CSVReader reader = new CSVReaderBuilder(new FileReader("src/main/resources/cardData.csv"))
                    .withSkipLines(cardID + 1).build();

            String[] nextline;
            nextline = reader.readNext();
            if(nextline != null) {
                switch (colorid) {
                    case 1: color = "red"; break;
                    case 2: color = "blue"; break;
                    case 3: color = "green"; break;
                    case 4: color = "yellow"; break;
                    case 5: color = "colorless"; break;
                }
                System.out.print("id: " + cid); // index is column number
                System.out.print(" | value: " + value); // index is column number
                System.out.print(" | color: " + color); // index is column number
                if (Main.debug) System.out.print(colorid);
                    if (Objects.equals(nextline[3], "1") && Main.debug) {
                        System.out.print("red");
                    }
                    else if (Objects.equals(nextline[3], "2")  && Main.debug) {
                        System.out.print("blue");
                }
                    else if (Objects.equals(nextline[3], "3") && Main.debug) {
                        System.out.print("green");
                }
                    else if (Objects.equals(nextline[3], "4") && Main.debug) {
                        System.out.print("yellow");
                }
                    else if (Objects.equals(nextline[3], "5") && Main.debug) {
                        System.out.print("wild");
                }
                    else if (Main.debug) {
                        System.out.print("Card color error.");
                    }
            }
        }catch (Exception e){
            System.out.println(e);
        }
        System.out.println("\nFinished.");
        return(cid);
    };

    public static void playCard(Player player) { // test
        if (Main.debug) System.out.println("playCardTest");
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
            if ((getCardInfo(cardID, 2) == 10 || getCardInfo(cardID, 2) == 11 || getCardInfo(cardID, 2) == 12 || getCardInfo(cardID, 2) == 13)) {
                switch (getCardInfo(cardID, 2)) {
                    case 10:
                        if (Main.debug) System.out.println("+2");
                        drawTwo = true;
                        break;
                    case 12:
                        if (Main.debug) System.out.println("Skip");
                        skip = true;
                        break;
                    case 13:
                        if (Main.debug) System.out.println("Reverse");
                        reverse = !reverse;
                        turnDirection *= -1;
                        break;
                    default:
                        System.out.println("Error indexing action card");
                        break;
                }
            }
            else if (getCardInfo(cardID, 2) >= 50) {
                if (Main.debug) System.out.println("powerCard");
                switch (getCardInfo(cardID, 2)) {
                    default:
                        System.out.println("Error indexing power card");
                        break;
                    case 51:
                        if (Main.debug) System.out.println("+4");
                        colorid = powerCards.drawFour(cardID);
                        if (Main.debug) System.out.println("colorid = " + colorid);
                        break;
                    case 52:
                        if (Main.debug) System.out.println("Wild");
                        colorid = powerCards.wild(cardID);
                        if (Main.debug) System.out.println("colorid = " + colorid);
                        break;
                }
            }

            // at the end, regardless of played card,
            // add card to discard pile, remove from player hand, value
            // (remember, hand array is just card id, so only card id is selected & added)
            value = getCardInfo(cardID, 2);
            GameData.discardPile.add(player.hand.get(selectionIndex)); // add to discard pile
            player.hand.remove(selectionIndex); // remove from player hand
            // color value set independently, to account for wild
            if (cardID < 50) {
                colorid = getCardInfo(cardID, 3);
            }
        }
        else {
            System.out.println("Invalid card.");
            playCard(player);
            return;
        }
    }

    public static boolean testPlayable(int cardID) {

        int playColor = 0, playValue = 0, discardValue = 0, discardColor = 0, playID = 0;

        try {
            CSVReader playableReader = new CSVReaderBuilder(new FileReader("src/main/resources/cardData.csv"))
                    .withSkipLines(cardID + 1).build();

            CSVReader discardReader = new CSVReaderBuilder(new FileReader("src/main/resources/cardData.csv"))
                    .withSkipLines(GameData.discardPile.peek() + 1).build();

            String[] nextlinePlay;
            nextlinePlay = playableReader.readNext();
            if (nextlinePlay != null) {
                playColor = Integer.parseInt(nextlinePlay[3]);
                playValue = Integer.parseInt(nextlinePlay[2]);
                playID = Integer.parseInt(nextlinePlay[1]);
            }

            String[] nextlineDiscard;
            nextlineDiscard = discardReader.readNext();
            if (nextlineDiscard != null) {
                discardColor =  Integer.parseInt(nextlineDiscard[3]);
                discardValue = Integer.parseInt(nextlineDiscard[2]);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        if (playColor == colorid || playValue == value || playColor == 5 || colorid == 5 || playID > 50) {
            if (Main.debug) System.out.println("True; testPlayable Finished.");
            return(true);
        }
        else {
            if (Main.debug) System.out.println("False; testPlayable Finished.");
            return(false);
        }
    }

    // Initialize discard pile
    public static Stack<Integer> discardInit() {
        discardPile.add(54); // add that index value to discard
        if (Main.debug) System.out.println("discardInit Finished" + discardPile);
        return discardPile;
    }

    // Initialize deck
    public static ArrayList<Integer> deckInit (int deckSize) {

        for(int i = 0; i <= deckSize; i++) {
            deck.add(i);
        }

        if (Main.debug) System.out.println("deckInit finished." + deck);
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

        if (Main.debug) {
            System.out.println(colorid);
            System.out.println(value);
            System.out.println(cid);
        }


        // while true, the game runs
        while (gameEnabled) {
            turnValue = Math.floorMod(turnCount, 4);
            if (Main.debug) System.out.println("turnCount: " + turnCount + " | turnValue: " + turnValue);

            if (turnValue == player1.turnValue) {
                System.out.println("Player 1 turn:");

                if (drawFour) {
                    player1.drawCard(4);
                    drawFour = false;
                    playCard(player1);
                }
                else if (drawTwo) {
                    player1.drawCard(2);
                    drawTwo = false;
                    playCard(player1);
                }
                else if (skip) {
                    skip = false;
                }
                else playCard(player1);
            }
            else if (turnValue == player2.turnValue) {
                System.out.println("Player 2 turn:");

                if (drawFour) {
                    player2.drawCard(4);
                    drawFour = false;
                    playCard(player2);
                }
                else if (drawTwo) {
                    player2.drawCard(2);
                    drawTwo = false;
                    playCard(player2);
                }
                else if (skip) {
                    skip = false;
                }
                else playCard(player2);
            }
            else if (turnValue == player3.turnValue) {
                System.out.println("Player 3 turn:");

                if (drawFour) {
                    player3.drawCard(4);
                    drawFour = false;
                    playCard(player3);
                }
                else if (drawTwo) {
                    player3.drawCard(2);
                    drawTwo = false;
                    playCard(player3);
                }
                else if (skip) {
                    skip = false;
                }
                else playCard(player3);
            }
            else if (turnValue == player4.turnValue) {
                System.out.println("Player 4 turn:");

                if (drawFour) {
                    player4.drawCard(4);
                    drawFour = false;
                    playCard(player4);
                }
                else if (drawTwo) {
                    player4.drawCard(2);
                    drawTwo = false;
                    playCard(player4);
                }
                else if (skip) {
                    skip = false;
                }
                else playCard(player4);
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
        initGame(7, 52);
    }
}
