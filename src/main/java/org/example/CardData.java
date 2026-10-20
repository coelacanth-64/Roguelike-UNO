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
                    name = nextLine[0];
                    id = nextLine[1];
                    value = nextLine[2];

                    switch (colorID = Integer.parseInt(nextLine[3])) {
                        case 1 -> color = "Red";
                        case 2 -> color = "Blue";
                        case 3 -> color = "Green";
                        case 4 -> color = "Yellow";
                        case 5 -> color = "Wild";
                        default -> System.out.println("Unknown color");
                    }

                    description = nextLine[4];
                    imagePath = nextLine[5];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name + " (id=" + id + ", value=" + value + ", color=" + color + ")";
    }
}