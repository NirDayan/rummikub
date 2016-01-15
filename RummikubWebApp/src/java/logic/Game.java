package logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import logic.tile.*;
import ws.rummikub.GameDetails;
import ws.rummikub.GameStatus;
import ws.rummikub.PlayerStatus;

public class Game {

    private final String name;
    private final ArrayList<Player> players;
    private final Deck tilesDeck;
    private final Board board;
    private Player currentPlayer;
    private Player winner;
    private final boolean isLoadedFromFile;
    private GameStatus status;
    private int joinedHumanPlayersNum;
    private final int humanPlayersNum;
    private final int computerizedPlayersNum;
    private static final int INITIAL_TILES_COUNT = 14;
    private static final int PUNISH_TILES_NUMBER = 3;
    private static final int MINIMUM_SUM_SEQUENCE_VALUE_FOR_FIRST_STEP = 30;
    private final List<Tile> currentPlayerAddedTiles;

    public Game(GameDetails gameDetails) {
        players = new ArrayList<>();
        currentPlayerAddedTiles = new ArrayList<>();
        tilesDeck = new Deck();
        board = new Board();

        name = gameDetails.getName();
        isLoadedFromFile = gameDetails.isLoadedFromXML();
        status = gameDetails.getStatus();
        humanPlayersNum = gameDetails.getHumanPlayers();
        computerizedPlayersNum = gameDetails.getComputerizedPlayers();
        joinedHumanPlayersNum = gameDetails.getJoinedHumanPlayers();
    }

    public void reset() {
        for (Player player : players) {
            player.reset();
        }
        board.reset();
        getTilesDeck().reset();
        currentPlayerAddedTiles.clear();
        winner = null;
        setCurrentPlayer(players.get(0));
        distributeTiles();
    }

    public void addPlayer(Player player) {
        if (currentPlayer == null) {
            currentPlayer = player;
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
        if (resignedPlayersCount == players.size() - 1) {
            return true;
        }

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
        currentPlayerAddedTiles.clear();
        int currPlayerIndex = players.indexOf(currentPlayer);
        if (currPlayerIndex == players.size() - 1) {
            setCurrentPlayer(players.get(0));
        } else {
            setCurrentPlayer(players.get(currPlayerIndex + 1));
        }
    }

    public Tile pullTileFromDeck(int playerID) {
        Tile tile = null;
        Player player = getPlayerByID(playerID);
        if (player != null && !getTilesDeck().isEmpty()) {
            tile = getTilesDeck().pullTile();
            player.addTile(tile);
        }

        return tile;
    }

    public boolean isPlayerFirstStep(int playerID) {
        Player player = getPlayerByID(playerID);

        return player.isFirstStep();
    }

    public boolean isPlayerResign(int playerID) {
        Player player = getPlayerByID(playerID);

        return player.isResign();
    }

    public List<Tile> punishPlayer(int id) {
        List<Tile> tilesToAdd = new ArrayList<>();
        Tile tile;
        for (int i = 0; i < PUNISH_TILES_NUMBER; i++) {
            tile = pullTileFromDeck(id);
            if (tile != null) {
                tilesToAdd.add(tile);
            }
        }

        return tilesToAdd;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Deck getTilesDeck() {
        return tilesDeck;
    }

    private void distributeTiles() {
        for (Player player : players) {
            for (int i = 0; i < INITIAL_TILES_COUNT; i++) {
                player.addTile(getTilesDeck().pullTile());
            }
        }
    }

    public boolean moveTile(MoveTileData data) {
        return board.moveTile(data);
    }

    public boolean addTile(int playerID, Tile tile, int sequenceIndex, int sequencePosition) {
        Player player = getPlayerByID(playerID);

        if (player != null && board.isTargetValid(sequenceIndex, sequencePosition)) {
            if (player.removeTile(tile)) {
                boolean isAddTileSuccess = board.addTile(sequenceIndex, sequencePosition, tile);
                if (isAddTileSuccess) {
                    currentPlayerAddedTiles.add(tile);
                }
                return isAddTileSuccess;
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

    public boolean isLoadedFromFile() {
        return isLoadedFromFile;
    }

    public Tile takeBackTile(int playerId, int sequenceIndex, int sequencePosition) {
        //If the current player id is not the playerId or there are no currentPlayerAddedTiles
        if (currentPlayerAddedTiles.isEmpty() || currentPlayer.getID() != playerId) {
            return null;
        }
        Tile tile = board.getTile(sequenceIndex, sequencePosition);
        //If the required tile is not moved into the board by the current player in the current turn, return null
        if (tile == null || !currentPlayerAddedTiles.contains(tile)) {
            return null;
        }
        tile = board.removeTile(sequenceIndex, sequencePosition);
        //If the remove is not valid, tile will be null
        if (tile == null) {
            return null;            
        }
        Player player = getPlayerByID(playerId);
        player.addTile(tile);
        
        return tile;
    }

    private boolean createSequenceFromTilesList(Player player, List<Tile> tiles) {
        if (tiles == null) {
            return false;
        }

        currentPlayerAddedTiles.addAll(tiles);
        Sequence sequence = new Sequence(tiles);
        player.removeTiles(tiles);
        board.addSequence(sequence);

        return true;
    }

    private boolean isFirstSequenceValid(Sequence sequence) {
        return (sequence.getValueSum() >= MINIMUM_SUM_SEQUENCE_VALUE_FOR_FIRST_STEP);
    }

    private Player getPlayerByID(int playerID) {
        for (Player pl : players) {
            if (pl.getID() == playerID) {
                return pl;
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public static boolean checkPlayersNameValidity(List<String> playersNames) {
        if (!playersNames.stream().noneMatch((name) -> (name.isEmpty()))) {
            return false;
        }
        //check names are unique
        Set<String> namesSet = new HashSet<>(playersNames);
        return namesSet.size() >= playersNames.size();
    }

    public int getHumanPlayersNum() {
        return humanPlayersNum;
    }

    public int getComputerizedPlayersNum() {
        return computerizedPlayersNum;
    }

    public int getJoinedHumanPlayersNum() {
        return joinedHumanPlayersNum;
    }

    public void incJoinedHumanPlayersNum() {
        joinedHumanPlayersNum++;
        if (joinedHumanPlayersNum == humanPlayersNum) {
            setGameActive();
        }
    }

    public void decJoinedHumanPlayersNum() {
        joinedHumanPlayersNum--;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus newStatus) {
        status = newStatus;
    }

    private void setGameActive() {
        status = GameStatus.ACTIVE;
        for (Player player : players) {
            player.setStatus(PlayerStatus.ACTIVE);
        }
    }
}
