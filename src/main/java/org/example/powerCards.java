package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.Scanner;

public class powerCards {

    public static int drawFour(int cardID) {
        if (Main.debug) System.out.println("powerCards +4");
        GameData.drawFour = true;
        return wild(cardID);
    }

    public static int wild(int cardID) {
        if (Main.debug) System.out.println("powerCards wild");

        System.out.println("What color would you like to choose?\n1. Red\n2. Blue\n3. Green\n4. Yellow\n");

        Scanner scanner = new Scanner(System.in);
        Integer colorSelection = scanner.nextInt();
        scanner.nextLine();

        switch (colorSelection) {
            case 1:
                if (Main.debug) System.out.println("Red in wild class");
                GameData.colorid = 1;
                return 1;
            case 2:
                if (Main.debug) System.out.println("Blue in wild class");
                GameData.colorid = 2;
                return 2;
            case 3:
                if (Main.debug) System.out.println("Green in wild class");
                GameData.colorid = 3;
                return 3;
            case 4:
                if (Main.debug) System.out.println("Yellow in wild class");
                GameData.colorid = 4;
                return 4;
            default:
                System.out.println("Invalid color selection");
                wild(cardID);
                break;
        }
        System.out.println("Color selection error");
        return 0;
    }
}
