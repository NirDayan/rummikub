Lior Halfon 305275349
Nir Dayan 303052674

Class To Run: \\JavaFXRummikub\src\javafxrummikub\JavaFXRummikub.java

Assumptions:
================================================================================================================================================
Part 1 assumptions (console application):
================================================================================================================================================

1. The Game will remember the last saved file per game, not per player.
2. The Project is tested with NetBeans, and JDK 8.
3. If player chooses to resign from the game, his tiles are staying with him and don't return back to the game.
4. If player creates a wrong sequence on his first step, he will be punished by taking 3 tiles from the deck.
5. If player performed any change on the board, he can't take tile from deck on the same step.
6. If all players resigned from game except ONE player, the game is finished and there is no winner [technical win is not considered]

================================================================================================================================================
Part 2 assumptions (JAVAFX Application):
================================================================================================================================================
1. The console application from part 1 is also included in the project and works with the same model (\\JavaFXRummikub\src\console\Rummikub.java).