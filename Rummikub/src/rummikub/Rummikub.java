package rummikub;

import controllers.console.GameMainController;
import controllers.console.InputOutputController;

public class Rummikub {
    public static void main(String[] args) {
        new GameMainController(new InputOutputController()).start();
    }     
}
