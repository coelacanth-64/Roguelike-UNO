package org.example;

import java.util.*;

/*
    Main should primarily be used to initialize the game itself, as well as passing specific
    arguments we may need for testing (ex. debug allows to see more console outputs to know what is being executed)
*/

public class Main {
    public static boolean debug = true;
    public static int handSize = 7;
    public static int deckSize = 54;
    public static int playerCount = 4;

    public static void main(String[] args) {
        GameData game = new GameData();
        Console console = new Console(game);
        // handSize, deckSize, numPlayers
        console.runConsoleGame(handSize, deckSize, playerCount);
        }
    }