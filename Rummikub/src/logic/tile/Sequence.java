package logic.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sequence {

    private List<Tile> tiles;

    public Sequence(Tile... tiles) {
        List<Tile> allTiles = new ArrayList<>();
        allTiles.addAll(Arrays.asList(tiles));
        this.tiles = allTiles;
    }

    public Sequence(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public boolean isValid() {
        SequenceValidator validator = new SequenceValidator(tiles);
        return validator.isValid();
    }

    public int getSize() {
        return tiles.size();
    }

    public int getValueSum() {
        int sum = 0;
        if (isSameValue()) {
            sum = getSameValueSum();
        }
        else {
            sum = getOrderedSum();
        }        
        
        return sum;
    }

    public boolean addTile(int index, Tile tile) {
        if (tile != null && (index <= tiles.size() && index >= 0)) {
            tiles.add(index, tile);

            return true;
        }
        return false;
    }

    public Tile removeTile(int index) {
        Tile tile = null;
        if (index < tiles.size() && index >= 0) {
            tile = tiles.remove(index);
        }

        return tile;
    }

    public Tile getTile(int index) {
        Tile tile = null;
        if (index < tiles.size() && index >= 0) {
            tile = tiles.get(index);
        }

        return tile;
    }

    public Sequence split(int index) {
        Sequence newSequence = null;

        if (index < tiles.size() && index >= 1) {
            newSequence = new Sequence(new ArrayList<>(tiles.subList(index, tiles.size())));
            tiles = new ArrayList<>(tiles.subList(0, index));
        }

        return newSequence;
    }

    public List<Tile> toList() {
        return tiles;
    }
    
    @Override
    public Sequence clone() {
        List<Tile> result = new ArrayList<>(tiles);
        return new Sequence(result);
    }
    
    //Use this function with the assumption the sequence is Valid
    private boolean isSameValue() {
        Map<Color, Integer> mapColors = new HashMap<>();
        Color color;
        int count;
        
        for (Tile tile : tiles) {
            if (!tile.isJoker()) {
                color = tile.getColor();
                if (mapColors.containsKey(color)) {
                    count = mapColors.get(color);
                    mapColors.put(color, count + 1);
                }
                else {
                    mapColors.put(tile.getColor(), 1);
                } 
            }                       
        }
        
        for (Integer ammount : mapColors.values()) {
            if (ammount > 1) {
                return false;
            }
        }
        
        return true;
    }
    
    //Use this function with the assumption the sequence is Valid
    private int getSameValueSum() {
        int value = 0;
        for (Tile tile : tiles) {
            if (!tile.isJoker()) {
                value = tile.getValue();
                break;
            }            
        }
        
        return (tiles.size() * value);
    }

    /**
     * Use this function with the assumption the sequence is Valid
     * And the sequence is consisted of different colors
     * @return true if sequence is ascending, otherwise false
     */
    private boolean isAscending() {
        if (!tiles.get(0).isJoker()) {//First tile is not Joker
            if (!tiles.get(1).isJoker()) { // First and second tiles are not Joker
                return tiles.get(1).getValue() > tiles.get(0).getValue();
            }
            else {
                if (!tiles.get(2).isJoker()) { //First and third tile are not Joker, second is Joker
                    return tiles.get(2).getValue() > tiles.get(0).getValue();
                }
                else {//First tile is not Joker, second tile is Joker, third tile is Joker
                    if (tiles.size() > 3) {//{Num1, J, J, ...}
                        return tiles.get(3).getValue() > tiles.get(0).getValue();
                    }                    
                    else {//{Number, J, J} and size is 3
                        return true;
                    }
                }
            }            
        }
        else {//First tile is Joker
            if (!tiles.get(1).isJoker()) {//First tile is Joker and second tile is not Joker
                if (!tiles.get(2).isJoker()) {//{J, Num1, Num2, ...}
                    return tiles.get(2).getValue() > tiles.get(1).getValue();
                }
                else {//First and third tiles are Joker and second tile is not Joker
                    if (tiles.size() > 3) {//{J, Number, J, ...}
                        return tiles.get(3).getValue() > tiles.get(1).getValue();                         
                    }
                    else {//{J, Number, J} and size is 3
                        return true;
                    }
                }
            }
            else {//First and second tiles are Joker
                if (tiles.size() > 3) {//{J, J, Num, ...}
                    return tiles.get(3).getValue() > tiles.get(2).getValue() ;
                }
                else { //{J, J, Num} and size is 3
                    return tiles.get(2).getValue() >= 3;
                }
            }
        }
    }
    
    //Use this function with the assumption the sequence is Valid
    private int getOrderedSum() {
        int sum = 0;
        boolean isAscending = isAscending();
        int factor = (isAscending) ? (1) : (-1);
        if (!tiles.get(0).isJoker()) {
            int firstValue = tiles.get(0).getValue();
            for (int i = 0; i < tiles.size(); i++) {
               sum += firstValue + (factor * i);
            }
        }
        else if (!tiles.get(1).isJoker()) {
            int secondValue = tiles.get(1).getValue();
            sum += secondValue + (factor);
            for (int i = 0; i < tiles.size() - 1; i++) {
               sum += secondValue + (factor * i);
            }
        }
        else if (tiles.size() == 3) {//{J, J, Number}
            sum = 3 * tiles.get(2).getValue();               
        }
        else {// {J, J, Num, Num, ...}
            int thirdValue = tiles.get(2).getValue();
            sum += thirdValue + (2 * factor) + (factor);            
            for (int i = 0; i < tiles.size() - 2; i++) {
               sum += thirdValue + (factor * i);
            }
        }
        
        return sum;
    }
}
