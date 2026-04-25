package org.example;

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
