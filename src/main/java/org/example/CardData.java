package org.example;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/*
    This class, similar to the Player class, is to make card interaction simpler, by reading through the cardData csv file
    This allows for easier accessing of different properties of the cards without needing to check the csv every time
    This class allows for storing of all of a card's information in one place
*/

public class CardData {

    public String name;
    public String id;
    public String value;
    public String color;
    public int colorID;
    public String description;
    public String imagePath;

    // Constructor that loads from CSV
    public CardData(int cardID) {
        loadFromCSV(cardID);
    }

    private void loadFromCSV(int cardID) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("cardData.csv")) {
            if (inputStream == null) {
                throw new IllegalStateException("cardData.csv not found in jar/classpath");
            }

            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 CSVReader csvReader = new CSVReaderBuilder(reader)
                         .withSkipLines(cardID + 1)
                         .build()) {

                String[] nextLine = csvReader.readNext();

                if (nextLine != null) {
                    name = nextLine[0]; // name is column 1 in csv
                    id = nextLine[1]; // card id is column 2
                    value = nextLine[2]; // card value (Ace, 1, King, etc) is column 3

                    switch (colorID = Integer.parseInt(nextLine[3])) { // color is column 4
                        case 1 -> color = "Red";
                        case 2 -> color = "Blue";
                        case 3 -> color = "Green";
                        case 4 -> color = "Yellow";
                        case 5 -> color = "Wild";
                        default -> System.out.println("Unknown color");
                    }

                    // description is column 5, could be used for a tooltip popup that explains special card effects
                    description = nextLine[4];

                    imagePath = nextLine[5]; // image for card is column 6
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // turn card details into a string
    @Override
    public String toString() {
        return name + " (" + id + ": " + color + " " + value + ")";
    }
}