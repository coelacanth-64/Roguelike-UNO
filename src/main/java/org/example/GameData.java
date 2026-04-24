package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.*;

public class GameData {
    private final Random generator = new Random(System.nanoTime());

    private final List<Integer> deck = new ArrayList<>();
    private final Deque<Integer> discardPile = new ArrayDeque<>();
    private final List<Player> players = new ArrayList<>();

    // get CardData from a cardID
    private final Map<Integer, CardData> cardCache = new HashMap<>();

    // card variables
    private int colorID = 5;
    private int value = 50;
    private int cardID = 54;

    // flags for current game state
    private boolean drawFour = false;
    private boolean drawTwo = false;
    private boolean skip = false;
    private boolean reverse = false;
    private boolean gameEnabled = true;
    int turnDirection = 1;

    public GameData() { }

    public CardData getCardData(int cardID) {
        return cardCache.computeIfAbsent(cardID, CardData::new);
    }

    // Initialize deck
    public void deckInit (int deckSize) {
        deck.clear();
        for (int i = 0; i <= deckSize; i++) deck.add(i);
        Collections.shuffle(deck, generator); // make deck shuffled
        if (Main.debug) System.out.println("deckInit Finished" + deck);
    }

    // Initialize discard pile
    public void discardInit() {
        discardPile.clear();
        discardPile.push(54); // add that index value to discard
        if (Main.debug) System.out.println("discardInit Finished" + discardPile);
    }

    /* Initialize game state */
    /* Initializes deck with one of each card,
       Initializes discard pile with a blank card,
       Initializes a given amount of players, and their hands taken from the deck, with a given hand size */
    public void initGame(int handSize, int deckSize, int numPlayers) {
        deckInit(deckSize);
        discardInit();
        players.clear();

        for (int i = 0; i < numPlayers; i++) {
            Player p = new Player();
            p.handInit(handSize, deck);
            p.turnValue = i;
            players.add(p);
        }


        if (Main.debug) {
            System.out.println(colorID);
            System.out.println(value);
            System.out.println(cardID);
        }
    }

    /* Query different values */
        public List<Player> getPlayers() {
            return Collections.unmodifiableList(players);
        }

        public List<Integer> getPlayerHand(int playerIndex) {
            return Collections.unmodifiableList(players.get(playerIndex).hand);
        }

        public Optional<Integer> getTopDiscard() {
            return discardPile.isEmpty() ? Optional.empty() : Optional.of(discardPile.peek());
        }

        public int getDeckSize() {
            return deck.size();
        }

        public boolean isGameEnabled() {
            return gameEnabled;
        }

        /* Game logic and actions */
        public PlayResult playCard(int playerIndex, int cardIndex, ColorChooser chooser) {
            if (Main.debug) System.out.println("playCard");
            Player p = players.get(playerIndex);
            if (cardIndex < 0 || cardIndex >= p.hand.size()) return PlayResult.invalid("Index out of range");
            int cardID = p.hand.get(cardIndex);
            if (!testPlayable(cardID)) return PlayResult.invalid("Card not playable");

            int cardValue = getCardInfo(cardID, 2);
            if (cardValue >= 10 && cardValue <= 13) {
                switch (cardValue) {
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
            else if (cardValue >= 50) {
                if (Main.debug) System.out.println("powerCard");
                switch (getCardInfo(cardID, 2)) {
                    default:
                        System.out.println("Error indexing power card");
                        break;
                    case 51:
                        if (Main.debug) System.out.println("+4");
                        colorID = powerCards.drawFour(cardID);
                        if (Main.debug) System.out.println("colorid = " + colorID);
                        break;
                    case 52:
                        if (Main.debug) System.out.println("Wild");
                        colorID = powerCards.wild(cardID);
                        if (Main.debug) System.out.println("colorid = " + colorID);
                        break;
                }
            }


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
            }

        }




    public CardData getCardInfo(int id, int index) {
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

    public static void playCard(Player player){ // test
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
            }

        int cardID = player.hand.get(selectionIndex);

        if (GameData.testPlayable(cardID)) {
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
