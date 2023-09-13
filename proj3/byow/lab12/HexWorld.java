package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int DR = 100;
    private static final int DG = 100;
    private static final int DB = 100;
    private final int width;
    private final int height;
    private final int size;
    private final TETile[][] world;
    private final Random random;

    public HexWorld(int size) {
        this.size = size;
        this.random = new Random();
        width = (3 * size - 2) + 4 * (2 * size - 1);
        height = size * 10;
        this.world = new TETile[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }

    private Supplier<TETile> getTETileSupplier(boolean variant) {
        int r = random.nextInt(6);
        TETile t;
        switch (r) {
            case 0:
                t = Tileset.FLOWER;
                break;
            case 1:
                t = Tileset.GRASS;
                break;
            case 2:
                t = Tileset.MOUNTAIN;
                break;
            case 3:
                t = Tileset.SAND;
                break;
            case 4:
                t = Tileset.TREE;
                break;
            case 5:
                t = Tileset.WATER;
                break;
            default:
                t = Tileset.NOTHING;
                break;
        }
        return variant ? () -> TETile.colorVariant(t, DR, DG, DB, random) :
                () -> t;
    }

    public void addHexagon(int x, int y, boolean variant) {
        Supplier<TETile> teTileSupplier = getTETileSupplier(variant);
        for (int i = 0; i < size; i++) {
            for (int j = 0, u = x + size - 1 - i, v = y + i; j < size + i * 2; j++, u++) {
                world[u][v] = teTileSupplier.get();
            }
        }
        for (int i = size - 1; i >= 0; i--) {
            for (int j = 0, u = x + size - 1 - i, v = y + size * 2 - 1 - i; j < size + i * 2; j++, u++) {
                world[u][v] = teTileSupplier.get();
            }
        }
    }

    public void draw(boolean variant) {
        TERenderer teRenderer = new TERenderer();
        teRenderer.initialize(width, height);
        int x = 0, y = size * 2;
        for (int i = 0, v = y; i < 3; i++, v += size * 2) {
            addHexagon(x, v, variant);
        }
        x += 2 * size - 1;
        y -= size;
        for (int i = 0, v = y; i < 4; i++, v += size * 2) {
            addHexagon(x, v, variant);
        }
        x += 2 * size - 1;
        y -= size;
        for (int i = 0, v = y; i < 5; i++, v += size * 2) {
            addHexagon(x, v, variant);
        }
        x += 2 * size - 1;
        y += size;
        for (int i = 0, v = y; i < 4; i++, v += size * 2) {
            addHexagon(x, v, variant);
        }
        x += 2 * size - 1;
        y += size;
        for (int i = 0, v = y; i < 3; i++, v += size * 2) {
            addHexagon(x, v, variant);
        }
        teRenderer.renderFrame(world);
    }

    public static void main(String[] args) {
        HexWorld hexWorld = new HexWorld(6);
        hexWorld.draw(false);
    }
}
