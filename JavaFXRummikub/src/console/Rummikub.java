package console;

import console.controller.GameMainController;
import console.controller.InputOutputController;

public class Rummikub {
    public static void main(String[] args) {
        new GameMainController(new InputOutputController()).start();
    }     
}
