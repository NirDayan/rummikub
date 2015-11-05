package logic.tile;

import java.util.Random;

public class TileGenerator {

    public static Tile generate() throws Exception {
        Random rand = new Random();
        Color color = Color.values()[rand.nextInt(3)];
        int value = rand.nextInt(12) + 1;
        return generateTile(color, value);
    }
    public static Tile generateJoker() throws Exception {
        return new JokerTile();
    }

    public static Tile[] generate(int count) throws Exception {
        Tile[] res = new Tile[count];
        for (int i = 0; i < count; i++) {
            res[i] = generate();
        }
        return res;
    }

    public static Tile generate(Color color, int value) throws Exception {
        return generateTile(color, value);
    }

    private static Tile generateTile(Color color, int value) throws Exception {
        return new Tile(color,value);
    }
}
