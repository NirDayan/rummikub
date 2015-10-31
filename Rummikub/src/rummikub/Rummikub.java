package rummikub;

import controllers.console.Controller;
import logic.GamesManager;

public class Rummikub {
    public static void main(String[] args) {        
        new GamesManager(new Controller()).start();
    }     
}
