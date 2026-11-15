package tanhuo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import tanhuo.Grid.Point;

final class TrailLayout {
    static final List<Point> STEPS = List.of(
        new Point(0, -1), new Point(1, 0), new Point(0, 1), new Point(-1, 0));
    private final Grid map;
    private final Point gate;
    private final Random random;
    private final Set<Point> terrain;
    private final List<Point> candidates;
    private Map<Point, Character> layout = Map.of();

    TrailLayout(Grid map, Point gate, Random random) {
        this.map = map;
        this.gate = gate;
        this.random = random;
        terrain = reachable(Set.of());
        candidates = terrain.stream().filter(point -> point.distanceTo(gate) >= 4).toList();
    }

    void nextDay() {
        Map<Point, Character> fresh = new HashMap<>();
        Set<Point> blocked = new HashSet<>();
        place('O', 4, fresh, blocked, true);
        place('^', 3, fresh, blocked, true);
        place('R', 5, fresh, blocked, true);
        place('H', 2, fresh, blocked, true);
        place('D', 2, fresh, blocked, false);
        place('C', 3, fresh, blocked, false);
        place('S', 2, fresh, blocked, false);
        place('V', 2, fresh, blocked, false);

        layout.keySet().forEach(point -> map.set(point, '.'));
        fresh.forEach(map::set);
        layout = fresh;
    }

    private void place(char tile, int count, Map<Point, Character> fresh,
                       Set<Point> blocked, boolean solid) {
        List<Point> shuffled = new ArrayList<>(candidates);
        Collections.shuffle(shuffled, random);
        for (Point point : shuffled) {
            if (fresh.containsKey(point) || layout.getOrDefault(point, '.') == tile) continue;
            if (solid) {
                blocked.add(point);
                if (reachable(blocked).size() != terrain.size() - blocked.size()) {
                    blocked.remove(point);
                    continue;
                }
            }
            fresh.put(point, tile);
            if (--count == 0) return;
        }
        throw new IllegalStateException("Trail does not have space for tile " + tile);
    }

    private Set<Point> reachable(Set<Point> blocked) {
        Set<Point> visited = new HashSet<>();
        var next = new ArrayDeque<Point>();
        next.add(gate);
        visited.add(gate);
        while (!next.isEmpty()) {
            Point point = next.removeFirst();
            for (Point step : STEPS) {
                Point neighbor = point.offset(step.x(), step.y());
                if (map.at(neighbor) != '#' && !blocked.contains(neighbor) && visited.add(neighbor)) {
                    next.addLast(neighbor);
                }
            }
        }
        return visited;
    }
}
