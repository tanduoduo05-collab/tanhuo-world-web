package tanhuo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import tanhuo.Grid.Point;

final class HomeCompanion {
    private static final List<Point> DIRECTIONS = List.of(
        new Point(0, 1), new Point(1, 0), new Point(-1, 0), new Point(0, -1));

    private Point position = HomeLand.COMPANION_HOME;
    private Point previous = position;
    private Point lastTile = position;
    private boolean facingLeft;
    private boolean warming;
    private int pauseTicks;

    Point position() {
        return position;
    }

    Point previous() {
        return previous;
    }

    boolean facingLeft() {
        return facingLeft;
    }

    String warmUp() {
        warming = true;
        return besideBrazier()
            ? "tanduoduo and tanhoohoo warm their hands together."
            : "tanhoohoo is walking over to the brazier.";
    }

    void leaveFire() {
        warming = false;
    }

    String tick(HomeLand home, Point player) {
        previous = position;
        if (warming && home.isFireLit() && player.equals(HomeLand.BRAZIER)) {
            if (besideBrazier()) return "";
            Point next = nextStepToBrazier(home, player);
            if (next == null) return "";
            walk(next);
            return besideBrazier() ? "tanhoohoo joins tanduoduo by the fire." : "";
        }
        warming = false;
        // Give the player time to choose Invite when they come close.
        if (position.distanceTo(player) <= 1) return "";
        if (pauseTicks > 0) {
            pauseTicks--;
            return "";
        }

        List<Point> directions = new ArrayList<>(DIRECTIONS);
        Collections.shuffle(directions);
        Point back = new Point(lastTile.x() - position.x(), lastTile.y() - position.y());
        if (directions.remove(back)) directions.add(back);
        for (Point direction : directions) {
            Point next = position.offset(direction.x(), direction.y());
            if (canWalk(home, next, player)) {
                walk(next);
                break;
            }
        }
        pauseTicks = ThreadLocalRandom.current().nextInt(1, 5);
        return "";
    }

    private boolean besideBrazier() {
        return position.distanceTo(HomeLand.BRAZIER) == 1;
    }

    private Point nextStepToBrazier(HomeLand home, Point player) {
        var queue = new ArrayDeque<Point>();
        Map<Point, Point> firstSteps = new HashMap<>();
        firstSteps.put(position, position);
        queue.add(position);
        while (!queue.isEmpty()) {
            Point current = queue.removeFirst();
            for (Point direction : DIRECTIONS) {
                Point next = current.offset(direction.x(), direction.y());
                if (firstSteps.containsKey(next) || !canWalk(home, next, player)) continue;
                Point first = current.equals(position) ? next : firstSteps.get(current);
                if (next.distanceTo(HomeLand.BRAZIER) == 1) return first;
                firstSteps.put(next, first);
                queue.addLast(next);
            }
        }
        return null;
    }

    private boolean canWalk(HomeLand home, Point point, Point player) {
        char tile = home.map.at(point);
        return ".12345P".indexOf(tile) >= 0 && !point.equals(player)
            && !home.farmyard.occupied(point);
    }

    private void walk(Point next) {
        lastTile = position;
        if (next.x() != position.x()) facingLeft = next.x() < position.x();
        position = next;
    }
}
