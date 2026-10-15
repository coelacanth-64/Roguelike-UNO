package org.example;

public class CardData {

    public String name = "Unknown";
    public String id = "0";
    public String value = "0";
    public String color = "Unknown";
    public int colorID = 0;
    public String description = "Unknown card";
    public String imagePath = null; // now nullable; image paths removed from catalog

    private static final String[][] CARD_CATALOG = new String[][] {
        {"red A", "0", "1", "1", "red A", null},
        {"red 2", "1", "2", "1", "red 2", null},
        {"red 3", "2", "3", "1", "red 3", null},
        {"red 4", "3", "4", "1", "red 4", null},
        {"red 5", "4", "5", "1", "red 5", null},
        {"red 6", "5", "6", "1", "red 6", null},
        {"red 7", "6", "7", "1", "red 7", null},
        {"red 8", "7", "8", "1", "red 8", null},
        {"red 9", "8", "9", "1", "red 9", null},
        {"red Plus2", "9", "10", "1", "red Plus2", null},
        {"red Skip", "10", "12", "1", "red Skip", null},
        {"red Reverse", "11", "13", "1", "red Reverse", null},
        {"blue A", "12", "1", "2", "blue A", null},
        {"blue 2", "13", "2", "2", "blue 2", null},
        {"blue 3", "14", "3", "2", "blue 3", null},
        {"blue 4", "15", "4", "2", "blue 4", null},
        {"blue 5", "16", "5", "2", "blue 5", null},
        {"blue 6", "17", "6", "2", "blue 6", null},
        {"blue 7", "18", "7", "2", "blue 7", null},
        {"blue 8", "19", "8", "2", "blue 8", null},
        {"blue 9", "20", "9", "2", "blue 9", null},
        {"blue Plus2", "21", "10", "2", "blue Plus2", null},
        {"blue Skip", "22", "12", "2", "blue Skip", null},
        {"blue Reverse", "23", "13", "2", "blue Reverse", null},
        {"green A", "24", "1", "3", "green A", null},
        {"green 2", "25", "2", "3", "green 2", null},
        {"green 3", "26", "3", "3", "green 3", null},
        {"green 4", "27", "4", "3", "green 4", null},
        {"green 5", "28", "5", "3", "green 5", null},
        {"green 6", "29", "6", "3", "green 6", null},
        {"green 7", "30", "7", "3", "green 7", null},
        {"green 8", "31", "8", "3", "green 8", null},
        {"green 9", "32", "9", "3", "green 9", null},
        {"green Plus2", "33", "10", "3", "green Plus2", null},
        {"green Skip", "34", "12", "3", "green Skip", null},
        {"green Reverse", "35", "13", "3", "green Reverse", null},
        {"yellow A", "36", "1", "4", "yellow A", null},
        {"yellow 2", "37", "2", "4", "yellow 2", null},
        {"yellow 3", "38", "3", "4", "yellow 3", null},
        {"yellow 4", "39", "4", "4", "yellow 4", null},
        {"yellow 5", "40", "5", "4", "yellow 5", null},
        {"yellow 6", "41", "6", "4", "yellow 6", null},
        {"yellow 7", "42", "7", "4", "yellow 7", null},
        {"yellow 8", "43", "8", "4", "yellow 8", null},
        {"yellow 9", "44", "9", "4", "yellow 9", null},
        {"yellow Plus2", "45", "10", "4", "yellow Plus2", null},
        {"yellow Skip", "46", "12", "4", "yellow Skip", null},
        {"yellow Reverse", "47", "13", "4", "yellow Reverse", null},
        {"Plus 4", "48", "51", "5", "Plus 4", null},
        {"Plus 4", "49", "51", "5", "Plus 4", null},
        {"Plus 4", "50", "51", "5", "Plus 4", null},
        {"Plus 4", "51", "51", "5", "Plus 4", null},
        {"Wild", "52", "52", "5", "Wild", null},
        {"Wild", "53", "52", "5", "Wild", null},
        {"Blank", "54", "0", "5", "Blank card", null}
    };

    public CardData(int cardID) {
        loadFromCatalog(cardID);
    }

    public CardData(String[] row, int cardID) {
        if (row != null && row.length >= 5) {
            bind(row, cardID);
        } else {
            loadFromCatalog(cardID);
        }
    }

    private void loadFromCatalog(int cardID) {
        if (cardID >= 0 && cardID < CARD_CATALOG.length) {
            bind(CARD_CATALOG[cardID], cardID);
        } else {
            setFallbackCard();
        }
    }

    /**
     * bind maps a CSV row (or catalog row) into the instance fields.
     * It parses colorID and sets the human-readable color name.
     */
    private void bind(String[] row, int cardID) {
        name = row[0];
        id = row[1];
        value = row[2];
        try {
            colorID = Integer.parseInt(row[3]);
        } catch (NumberFormatException e) {
            colorID = 5; // default to Wild if malformed
        }
        color = colorName(colorID);
        description = row.length > 4 ? row[4] : "";
        // imagePath intentionally nullable; treat empty or missing as null
        imagePath = (row.length > 5 && row[5] != null && !row[5].isEmpty()) ? row[5] : null;

        if (cardID >= CARD_CATALOG.length) {
            setFallbackCard();
        }
    }

    private void setFallbackCard() {
        name = "Unknown";
        id = "-1";
        value = "0";
        colorID = 5;
        color = "Wild";
        description = "Unknown card";
        imagePath = null;
    }

    private static String colorName(int colorID) {
        return switch (colorID) {
            case 1 -> "Red";
            case 2 -> "Blue";
            case 3 -> "Green";
            case 4 -> "Yellow";
            case 5 -> "Wild";
            default -> "Unknown";
        };
    }

    @Override
    public String toString() {
        return name + " (id=" + id + ", value=" + value + ", color=" + color + ")";
    }
}
