Lior Halfon 305275349
Nir Dayan 303052674

Run Webservice Server: \\RummikubWebApp
Class To Run Webservice Client: \\JavaFXRummikub\src\javafxrummikub\JavaFXRummikub.java

We deployed our Server on GoogleCloud under the URL: http://104.154.83.211/rummikub/RummikubWS
You can connect to the remote server by using the below parameters:
*** Server IP Address: 104.154.83.211 *** 
*** Server Port: 80 *** 

Assumptions:
1. The Project is tested with NetBeans, and JDK 8.
2. If player chooses to resign from the game, his tiles are staying with him and don't return back to the game.
3. If player creates a wrong sequence on his first step, he will be punished by taking 3 tiles from the deck.
4. If player performed any change on the board, he can't take tile from deck on the same step.
5. If all players resigned from game except ONE player, the game is finished and there is no winner [technical win is not considered]
6. The web client cannot run on the GoogleCloud server.
