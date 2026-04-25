package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.*;

/*
    This class is where all the back-end game data & logic is stored. Do not use this class for console interaction
    This class is here to allow for simpler adding of new cards, as well as making UI easier to implement by using just the necessary values.
*/

public class GameData {
    private final Random generator = new Random(System.nanoTime());

    private final ArrayList<Integer> deck = new ArrayList<>();
    private final Deque<Integer> discardPile = new ArrayDeque<>();
    private final List<Player> players = new ArrayList<>();

    // get CardData from a cardID
    private final Map<Integer, CardData> cardCache = new HashMap<>();

    // card variables
    private int colorID = 5;
    private int value = 50;
    private int cardID = 54;

    // flags for current game state
    boolean drawFour = false;
    boolean drawTwo = false;
    boolean skip = false;
    boolean reverse = false;
    private boolean gameEnabled = true;
    int turnDirection = 1;

    public GameData() { }

    public CardData getCardData(int cardID) {
        return cardCache.computeIfAbsent(cardID, CardData::new);
    }

    // Initialize deck
    public void deckInit (int deckSize) {
        deck.clear();
        for (int i = 0; i < deckSize; i++) {
            deck.add(i);
        }
        Collections.shuffle(deck, generator); // make deck shuffled
        if (Main.debug) {
            System.out.println("deckInit Finished" + deck);
            System.out.println("deckInit: deck id=" + System.identityHashCode(deck) + " size=" + deck.size());
            for (int i = 0; i < deck.size(); i++) {
                if (deck.get(i) == null) {
                    System.err.println("deckInit: NULL at index " + i);
                }
            }
        }
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

        if (Main.debug) {
            System.out.println("INIT deck size=" + deck.size() + " deck contents sample: " + (deck.size() > 10 ? deck.subList(0, 10) : deck));
            System.out.println("INIT deck identity: " + System.identityHashCode(deck));
        }

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

        public List<Integer> getDeck() {
            return deck;
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
            } else if (cardValue >= 50) {
                if (Main.debug) System.out.println("powerCard");
                switch (getCardInfo(cardID, 2)) {
                    default:
                        System.out.println("Error indexing power card");
                        break;
                    case 51:
                        if (Main.debug) System.out.println("+4");
                        drawFour = true;
                        colorID = chooser.chooseColor(playerIndex, cardID);
                        if (Main.debug) System.out.println("colorid = " + colorID);
                        break;
                    case 52:
                        if (Main.debug) System.out.println("Wild");
                        colorID = chooser.chooseColor(playerIndex, cardID);
                        if (Main.debug) System.out.println("colorid = " + colorID);
                        break;
                }
            }

            //value = cardValue; // set
            discardPile.push(p.hand.remove(cardIndex)); // add played card to discard pile
            if (cardID < 50) colorID = getCardInfo(cardID, 3); // get color to played card color

            boolean playerWon = p.hand.isEmpty();
            if (playerWon) gameEnabled = false;
            return PlayResult.winner(cardID, playerWon);
        }

        public List<Integer> drawCards(int playerIndex, int count) {
            if (Main.debug) System.out.println("drawCards called: deck size before=" + deck.size() + " deckId=" + System.identityHashCode(deck));
            Player p = players.get(playerIndex);

            List<Integer> drawnCards = new ArrayList<>();

            for (int i = 0; i < count && !deck.isEmpty(); i++) {
                Integer removed = deck.remove(deck.size() - 1);
                System.out.println("  drawCards removed: " + removed + " (deckId=" + System.identityHashCode(deck) + ")");
                if (removed == null) {
                    System.err.println("  drawCards: removed NULL from deck! (playerIndex=" + playerIndex + ")");
                    // do not add null to drawnCards; continue to next removal
                    continue;
                }
                drawnCards.add(removed);
            }

            p.hand.addAll(drawnCards);

            return drawnCards;
        }

    public int getCardInfo(int id, int index) {
        CardData cd = getCardData(id);
        try {
            switch (index) {
                case 1: return Integer.parseInt(cd.id);
                case 2: return Integer.parseInt(cd.value);
                case 3: return cd.colorID;
                default: return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean testPlayable(int cardID) {

        // first card always playable
        if (discardPile.isEmpty())
            return true;

        CardData play = getCardData(cardID);
        CardData discard = getCardData(discardPile.peek());
        int playColor = play.colorID; // played card color
        int playValue = Integer.parseInt(play.value); // played card value
        int playID = Integer.parseInt(play.id); // played card ID (for power cards)

        /* check playable based on conditions:
         normal case: played card color == discard pile color  ||   played card value == discard pile value
         special case: played card color is special || discard pile color is special || played card is a power card (id > 50)
        */
        return (playColor == colorID || playValue == value || playColor >= 5 || colorID >= 5 || playID > 50);
    }

    public boolean checkHandPlayable(int playerIndex) {
       int unplayableCards = 0;

       if (players.isEmpty()) // if no players: false
           return false;

       Player p = players.get(playerIndex);

       if (p.hand.isEmpty()) // if player hand empty: false
           return false;

       if (discardPile.isEmpty()) // first card is playable always
           return true;

        for (int i = 0; i < p.hand.size(); i++) {
           if (Main.debug) System.out.println("Checking card index" + i);
           if (!testPlayable(p.hand.get(i))) {
               unplayableCards++;
           }
       }
        if (unplayableCards == p.hand.size()) {
            if (Main.debug) System.out.println("Unplayable hand");
            return false;
        }
        else {
            if (Main.debug) System.out.println("Playable hand.");
            return true;
        }
    }

        public int turnIndex(int turnCount) {
            if (players.isEmpty())
                return -1; // if no players, error
            else
                return Math.floorMod(turnCount, players.size()); // else, return the turnIndex (range: 1 -> player count)
        }
    }
