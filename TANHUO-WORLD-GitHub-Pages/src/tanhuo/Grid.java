package tanhuo;

import java.util.ArrayList;
import java.util.List;

final class Grid {
    record Point(int x, int y) {
        Point offset(int dx, int dy) {
            return new Point(x + dx, y + dy);
        }

        int distanceTo(Point other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y);
        }
    }

    private final char[][] tiles;

    Grid(String... rows) {
        tiles = new char[rows.length][];
        for (int y = 0; y < rows.length; y++) {
            tiles[y] = rows[y].toCharArray();
        }
    }

    char at(Point point) {
        if (point.y < 0 || point.y >= tiles.length) return '#';
        char[] row = tiles[point.y];
        if (point.x < 0 || point.x >= row.length) return '#';
        return row[point.x];
    }

    void set(Point point, char tile) {
        tiles[point.y][point.x] = tile;
    }

    List<Point> locationsOf(char tile) {
        List<Point> result = new ArrayList<>();
        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[y].length; x++) {
                if (tiles[y][x] == tile) result.add(new Point(x, y));
            }
        }
        return List.copyOf(result);
    }

    List<String> render(Point player, Point companion) {
        List<String> rows = new ArrayList<>();
        for (int y = 0; y < tiles.length; y++) {
            char[] row = tiles[y].clone();
            if (companion != null && companion.y == y) row[companion.x] = 'T';
            if (player != null && player.y == y) row[player.x] = '@';
            rows.add(new String(row));
        }
        return rows;
    }
}
