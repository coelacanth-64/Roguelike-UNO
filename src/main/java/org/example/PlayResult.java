package org.example;

/*
    The purpose of this class is to provide a predictable return value for any play card action
    This is where special effect types can be chosen based on a played card, to be then given to powerCards
    This class just determines what each given played card event does
*/

public class PlayResult {
    public final boolean winner;
    public final String message;
    public final Integer playedCardID;
    public final boolean playerWon;

    private PlayResult(boolean winner, String message, Integer playedCardId, boolean playerWon) {
        this.winner = winner;
        this.message = message;
        this.playedCardID = playedCardId;
        this.playerWon = playerWon;
    }
    public static PlayResult winner(int cardId, boolean playerWon) {
        return new PlayResult(true, "Played", cardId, playerWon);
    }
    public static PlayResult invalid(String message) {
        return new PlayResult(false, message, null, false);
    }
}
