package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;

public class powerCards {

    public static boolean drawFour(int cardID) {
        if (GameData.debug) System.out.println("powerCards +4");
        GameData.drawFour = true;
        return true;
    }

    public static boolean wild(int cardID) {
        if (GameData.debug) System.out.println("powerCards wild");

        try {
            CSVReader reader = new CSVReaderBuilder(new FileReader("src/main/resources/cardData.csv"))
                    .withSkipLines(cardID + 1).build();
            String[] nextline;
            nextline = reader.readNext();
        } catch (Exception e) {
            System.out.println(e);


        }

        return true;

    }
}
