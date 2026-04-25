package org.example;

import java.util.*;

public class Main {
    public static boolean debug = true;

    public static void main(String[] args) {
        GameData game = new GameData();
        Console console = new Console(game);
        // handSize, deckSize, numPlayers
        console.runConsoleGame(2, 52, 4);
        }
    }