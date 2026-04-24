package org.example;

import com.opencsv.*;
import java.io.FileReader;

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
        try {
            CSVReader reader = new CSVReaderBuilder(
                    new FileReader("src/main/resources/cardData.csv")
            ).withSkipLines(cardID + 1).build();

            String[] nextline = reader.readNext();

            if (nextline != null) {
                name = nextline[0];
                id = nextline[1];
                value = nextline[2];
                switch (colorID = Integer.parseInt(nextline[3])) {
                    case 1: color = "Red";
                        break;
                    case 2: color = "Blue";
                        break;
                    case 3: color = "Green";
                        break;
                    case 4: color = "Yellow";
                        break;
                    case 5: color = "None";
                        break;
                    default: System.out.println("Error");
                        break;
                }
                description = nextline[4];
                imagePath = nextline[5];
            }

        } catch (Exception e) {
            System.out.println("Error loading card: " + e);
        }
    }
}