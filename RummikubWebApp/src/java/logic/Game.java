package logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import logic.persistency.FileDetails;
import logic.tile.*;

public class Game {
    private final String name;
    private final ArrayList<Player> players;
    private final Deck tilesDeck;
    private final Board board;
    private Player currentPlayer;
    private Player winner;
    private FileDetails savedFileDetails;
    private static final int INITIAL_TILES_COUNT = 14;
    private static final int PUNISH_TILES_NUMBER = 3;
    private static final int MINIMUM_SUM_SEQUENCE_VALUE_FOR_FIRST_STEP = 30;

    public Game(GameDetails gameDetails) {
        players = new ArrayList<>();
        currentPlayer = null;
        tilesDeck = new Deck();
        board = new Board();
        name = gameDetails.getGameName();
        savedFileDetails = gameDetails.getSavedFileDetails();

        createPlayers(gameDetails);
    }

    public void reset() {
        for (Player player : players) {
            player.reset();
        }
        board.reset();
        getTilesDeck().reset();
        winner = null;
        setCurrentPlayer(players.get(0));
        distributeTiles();
    }

    public void addPlayer(Player player) {
        if (currentPlayer == null) {
            setCurrentPlayer(player);
        }
        players.add(player);
    }

    public boolean checkIsGameOver() {
        //if there is a winner
        if (currentPlayer.isFinished()) {
            winner = currentPlayer;
            return true;
        }
        //if the deck is empty
        if (getTilesDeck().isEmpty()) {
            return true;
        }
        //if all players resigned
        int resignedPlayersCount = 0;
        for (Player player : players) {
            if (player.isResign()) {
                resignedPlayersCount++;
            }
        }
        if (resignedPlayersCount == players.size() - 1)
            return true;

        return false;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public Player getWinner() {
        return winner;
    }

    public void moveToNextPlayer() {
        int currPlayerIndex = players.indexOf(currentPlayer);
        if (currPlayerIndex == players.size() - 1) {
            setCurrentPlayer(players.get(0));
        }
        else {
            setCurrentPlayer(players.get(currPlayerIndex + 1));
        }
    }

    public void pullTileFromDeck(int playerID) {
        Player player = getPlayerByID(playerID);
        if (player != null && !getTilesDeck().isEmpty())
            player.addTile(getTilesDeck().pullTile());
    }

    public boolean isPlayerFirstStep(int playerID) {
        Player player = getPlayerByID(playerID);

        return player.isFirstStep();
    }

    public boolean isPlayerResign(int playerID) {
        Player player = getPlayerByID(playerID);

        return player.isResign();
    }

    public void punishPlayer(int id) {
        for (int i = 0; i < PUNISH_TILES_NUMBER; i++) {
            pullTileFromDeck(id);
        }
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Deck getTilesDeck() {
        return tilesDeck;
    }

    public FileDetails getSavedFileDetails() {
        return savedFileDetails;
    }

    public void setSavedFileDetails(FileDetails savedFileDetails) {
        this.savedFileDetails = savedFileDetails;
    }

    private void distributeTiles() {
        for (Player player : players) {
            for (int i = 0; i < INITIAL_TILES_COUNT; i++) {
                player.addTile(getTilesDeck().pullTile());
            }
        }
    }

    private void createPlayers(GameDetails gameDetails) {
        Player currPlayer;

        for (PlayerDetails playerDetails : gameDetails.getPlayersDetails()) {
            currPlayer = new Player(playerDetails);
            addPlayer(currPlayer);
        }
    }

    public boolean moveTile(MoveTileData data) {
        return board.moveTile(data);
    }

    public boolean addTile(int playerID, MoveTileData addTileData) {
        Player player = getPlayerByID(playerID);
        Tile tile;
        int seqIndex = addTileData.getTargetSequenceIndex();
        int seqPosition = addTileData.getTargetSequencePosition();

        if (player != null && board.isTargetValid(seqIndex, seqPosition)) {
            tile = player.removeTile(addTileData.getSourceSequencePosition());
            if (tile != null) {
                return board.addTile(addTileData.getTargetSequenceIndex(),
                        addTileData.getTargetSequencePosition(), tile);
            }
        }

        return false;
    }

    public void playerResign(int playerID) {
        Player player = getPlayerByID(playerID);
        if (player != null) {
            player.setIsResign(true);
        }
    }

    /**
     * player creates a new sequence on the board.
     *
     * @param playerID
     * @param tilesIndices
     * @return true if a new sequence was created, false otherwise
     */
    public boolean createSequence(int playerID, List<Integer> tilesIndices) {
        Player player = getPlayerByID(playerID);
        if (player == null || tilesIndices == null)
            return false;

        return createSequenceFromTileIndices(player, tilesIndices);
    }
    
    public boolean checkSequenceValidity(int playerID, List<Tile> tiles) {
        Player player = getPlayerByID(playerID);
        if (tiles == null || player == null) {
            return false;
        }

        Sequence sequence = new Sequence(tiles);
        if (!sequence.isValid()) {
            return false;
        }            
        if (player.isFirstStep() && !isFirstSequenceValid(sequence)) {
            return false;
        }
        
        return true;
    }

    public boolean createSequenceByTilesList(int playerID, List<Tile> tiles) {
        Player player = getPlayerByID(playerID);
        if (player == null || tiles == null) {
            return false;
        }

        return createSequenceFromTilesList(player, tiles);
    }
    
    public boolean createSequenceByPlayerTile(int playerID, int tileIndex) {
        Player player = getPlayerByID(playerID);
        if (player == null) {
            return false;
        }
        
        List<Integer> tilesIndicesList = new ArrayList<>();
        tilesIndicesList.add(tileIndex);
        List<Tile> tilesToAdd = player.getTilesByIndices(tilesIndicesList);
        if (tilesToAdd == null) {
            return false;
        }
        
        Sequence sequence = new Sequence(tilesToAdd);        
        player.removeTilesByIndices(tilesIndicesList);
        board.addSequence(sequence);

        return true;
    }
    
    public void storeBackup() {
        board.storeBackup();
        currentPlayer.storeBackup();
    }
    
    public void restoreFromBackup() {
        board.restoreFromBackup();
        currentPlayer.restoreFromBackup();
    }
    
    public void setPlayerCompletedFirstStep(int playerID) {
        Player player = getPlayerByID(playerID);
        if (player != null) {
            player.setFirstStepCompleted(true);
        }
    }

    private boolean createSequenceFromTileIndices(Player player, List<Integer> tilesIndices) {
        List<Tile> preSequence = player.getTilesByIndices(tilesIndices);
        if (preSequence == null) {
            return false;
        }

        Sequence sequence = new Sequence(preSequence);
        if (!sequence.isValid())
            return false;
        if (player.isFirstStep() && !isFirstSequenceValid(sequence))
            return false;

        player.removeTilesByIndices(tilesIndices);
        board.addSequence(sequence);

        return true;
    }

    private boolean createSequenceFromTilesList(Player player, List<Tile> tiles) {
        if (tiles == null)
            return false;

        Sequence sequence = new Sequence(tiles);
        if (!sequence.isValid())
            return false;
        if (player.isFirstStep() && !isFirstSequenceValid(sequence))
            return false;

        player.removeTiles(tiles);
        board.addSequence(sequence);

        return true;
    }

    private boolean isFirstSequenceValid(Sequence sequence) {
        return (sequence.getValueSum() >= MINIMUM_SUM_SEQUENCE_VALUE_FOR_FIRST_STEP);
    }

    private Player getPlayerByID(int playerID) {
        for (Player pl : players) {
            if (pl.getID() == playerID)
                return pl;
        }

        return null;
    }

    public String getName() {
        return name;
    }
    
    public static boolean checkPlayersNameValidity(List<String> playersNames) {
        for (String name : playersNames) {
            if (name.isEmpty()) {
                return false;
            }
        }
        //check names are unique
        Set<String> namesSet = new HashSet<>(playersNames);
        if (namesSet.size() < playersNames.size()) {
            return false;
        }
        return true;
    }
}
