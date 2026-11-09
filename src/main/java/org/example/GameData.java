package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.*;

/*
    This class is where all the back-end game data & logic is stored. Do not use this class for console interaction
    This class is here to allow for simpler adding of new cards and mechanics,
    as well as making UI easier to implement by using just the necessary outputs.
*/

public class GameData {
    private final Random generator = new Random(System.nanoTime());

    private final ArrayList<Integer> deck = new ArrayList<>();
    private final Deque<Integer> discardPile = new ArrayDeque<>();
    private final List<Player> players = new ArrayList<>();

    // get CardData from a cardID
    private final Map<Integer, CardData> cardCache = new HashMap<>();

    // Active color chosen for the current turn (particularly important after a wild).
    private int colorID = 5;
    private int value = 0;
    private int cardID = 0;

    // Pending penalty cards to draw at the beginning of the next turn.
    int nextTurnDrawCount = 0;
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
        colorID = 5;
        value = 0;
    }

    /* Initialize game state */
    /* Initializes deck with one of each card,
       Initializes discard pile with a blank card,
       Initializes a given amount of players, and their hands taken from the deck, with a given hand size */
    public void initGame(int handSize, int deckSize, int numPlayers) {
        deckInit(deckSize);
        discardInit();
        players.clear();
        gameEnabled = true;
        turnDirection = 1;
        nextTurnDrawCount = 0;
        skip = false;
        reverse = false;

        if (Main.debug) {
            System.out.println("INIT deck size=" + deck.size() + " deck contents sample: " + (deck.size() > 10 ? deck.subList(0, 10) : deck));
            System.out.println("INIT deck identity: " + System.identityHashCode(deck));
        }

        for (int i = 0; i < numPlayers; i++) { //A - Edited to add the Bot
            //Player p = new Player();
            // p.handInit(handSize, deck);
            // p.turnValue = i;
            //players.add(p);
            Player p;
            if (i == Main.botPlayerIndex){
                p = new Bot();
            }else{
                p = new Player();
            }
            p.handInit(handSize, deck);
            p.turnValue = i;
            players.add(p); // A - Player 5 is initialized as the Bot
        }
        
        
        if (Main.debug) {
            System.out.println(colorID);
            System.out.println(value);
            System.out.println(cardID);
        }
    }

    // return different values
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public List<Integer> getDeck() {
        return deck;
    }

    public List<Integer> getPlayerHand(int playerIndex) {
        return Collections.unmodifiableList(players.get(playerIndex).hand);
    }

    public int getTopDiscard() {
        if (discardPile.peek() != null)
            return discardPile.peek();
        else
            return -1;
    }

    public int getDeckSize() {
        return deck.size();
    }

    public boolean isGameEnabled() {
        return gameEnabled;
    }

    // Game logic and actions
    public PlayResult playCard(int playerIndex, int cardIndex, ColorChooser chooser) {
        if (Main.debug) System.out.println("playCard");
        if (playerIndex < 0 || playerIndex >= players.size()) return PlayResult.invalid("Invalid player");
        Player p = players.get(playerIndex);
        if (cardIndex < 0 || cardIndex >= p.hand.size()) return PlayResult.invalid("Index out of range");
        int playedCardID = p.hand.get(cardIndex);
        // Always enforce the same rule here, regardless of which UI/console path invokes playCard.
        if (!testPlayable(playedCardID)) return PlayResult.invalid("Card not playable");

        int cardValue = getCardInfo(playedCardID, 2);

        // this is where cards with special effects go that aren't necessarily powercards
        if (cardValue >= 10 && cardValue <= 13) {
            switch (cardValue) {
                case 10: // id 10 = +2
                    if (Main.debug) System.out.println("+2");
                    nextTurnDrawCount = 2;
                    break;
                case 12: // id 12 = skip
                    if (Main.debug) System.out.println("Skip");
                    skip = true;
                    break;
                case 13: // id 13 = reverse
                    if (Main.debug) System.out.println("Reverse");
                    reverse = !reverse;
                    turnDirection *= -1;
                    break;
                default:
                    System.out.println("Error indexing action card");
                    break;
            }
        } else if (isWildOrPlusFour(playedCardID)) {
            if (cardValue == 51) {
                if (Main.debug) System.out.println("+4");
                nextTurnDrawCount = 4;
            } else if (Main.debug) {
                System.out.println("Wild");
            }
            int chosenColor = chooser == null ? 1 : chooser.chooseColor(playerIndex, playedCardID);
            colorID = (chosenColor >= 1 && chosenColor <= 4) ? chosenColor : 1;
            if (Main.debug) System.out.println("colorid = " + colorID);
        }

        discardPile.push(p.hand.remove(cardIndex));
        if (!isWildOrPlusFour(playedCardID)) {
            colorID = getCardInfo(playedCardID, 3);
        }
        value = cardValue;

        boolean playerWon = p.hand.isEmpty();
        if (playerWon) gameEnabled = false;
        return PlayResult.winner(playedCardID, playerWon);
    }

    /**
     * Recycles the discard pile into the draw deck, leaving its top card in place.
     * Returns false if there are no discard cards available to recycle.
     */
    private boolean refillDeckFromDiscard() {
        if (discardPile.size() <= 1) {
            return false;
        }

        Integer topCard = discardPile.pop();
        while (!discardPile.isEmpty()) {
            deck.add(discardPile.pop());
        }
        discardPile.push(topCard);
        Collections.shuffle(deck, generator);

        if (Main.debug) {
            System.out.println("Refilled deck from discard pile; deck size=" + deck.size());
        }
        return !deck.isEmpty();
    }

    public List<Integer> drawCards(int playerIndex, int count) {
        if (playerIndex < 0 || playerIndex >= players.size() || count <= 0) {
            return Collections.emptyList();
        }
        if (Main.debug) System.out.println("drawCards called: deck size before=" + deck.size() + " deckId=" + System.identityHashCode(deck));
        Player p = players.get(playerIndex);

        List<Integer> drawnCards = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            // Refill on demand, including when a multi-card draw would exhaust
            // the deck partway through (for example, a +2 or +4 effect).
            if (deck.isEmpty() && !refillDeckFromDiscard()) {
                break;
            }

            Integer removed = deck.remove(deck.size() - 1);
            if (Main.debug)
                System.out.println("  drawCards removed: " + removed + " (deckId=" + System.identityHashCode(deck) + ")");
            if (removed == null) {
                System.err.println("  drawCards: removed NULL from deck! (playerIndex=" + playerIndex + ")");
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

    private boolean isWildOrPlusFour(int cardID) {
        int cardValue = getCardInfo(cardID, 2);
        return cardValue == 51 || cardValue == 52;
    }

    public boolean testPlayable(int cardID) {
        if (cardID < 0) return false;
        if (isWildOrPlusFour(cardID)) {
            return true;
        }

        if (discardPile.isEmpty()) {
            return true;
        }

        int topCardID = discardPile.peek();
        // 54 is the game's initial blank discard placeholder, before any card has been played.
        if (topCardID == 54) {
            return true;
        }

        CardData play = getCardData(cardID);
        CardData topDiscard = getCardData(topCardID);

        // After a wild, the chosen color is the only matching condition for
        // ordinary cards; matching the wild's printed value is not permitted.
        if (isWildOrPlusFour(topCardID)) {
            return play.colorID == colorID;
        }

        int playValue = getCardInfo(cardID, 2);
        int topDiscardValue = getCardInfo(topCardID, 2);
        return play.colorID == topDiscard.colorID || playValue == topDiscardValue;
    }

    public boolean checkHandPlayable(int playerIndex) {
        if (players.isEmpty() || playerIndex < 0 || playerIndex >= players.size()) {
            return false;
        }

        Player player = players.get(playerIndex);
        for (int cardID : player.hand) {
            if (testPlayable(cardID)) {
                return true;
            }
        }
        return false;
    }

    public int turnIndex(int turnCount) {
        if (players.isEmpty())
            return -1;
        return Math.floorMod(turnCount, players.size());
    }
}
