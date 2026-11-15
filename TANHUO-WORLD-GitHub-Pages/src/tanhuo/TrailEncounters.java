package tanhuo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import tanhuo.Grid.Point;
import tanhuo.TrailTarget.Kind;

final class TrailEncounters {
    record Strike(Point target, String message) {}

    private final Grid map;
    private final List<TrailTarget> active = new ArrayList<>();

    TrailEncounters(Grid map) {
        this.map = map;
    }

    void reset() {
        active.clear();
        for (Point cow : map.locationsOf('H')) active.add(new TrailTarget(cow, Kind.WILD_COW));
    }

    void moveCows(Point player, Point companion) {
        for (TrailTarget cow : active) {
            if (cow.kind != Kind.WILD_COW) continue;

            Point from = cow.position;
            Point to = cow.canLeadHome() ? from : chooseCowStep(from, player, companion);
            cow.moveTo(to);
            if (!from.equals(to)) {
                map.set(from, '.');
                map.set(to, 'H');
            }
        }
    }

    private Point chooseCowStep(Point from, Point player, Point companion) {
        int distance = from.distanceTo(player);
        boolean fleeing = distance <= 3;
        if (!fleeing && ThreadLocalRandom.current().nextInt(4) != 0) return from;

        List<Point> directions = new ArrayList<>(TrailLayout.STEPS);
        Collections.shuffle(directions);
        for (Point direction : directions) {
            Point next = from.offset(direction.x(), direction.y());
            if (map.at(next) != '.' || next.equals(player) || next.equals(companion)) continue;
            if (next.distanceTo(OutsideWorld.GATE) < 4) continue;
            if (!fleeing || next.distanceTo(player) > distance) return next;
        }
        return from;
    }

    List<TrailTarget.View> view() {
        return active.stream().map(TrailTarget::view).toList();
    }

    boolean occupied(Point point) {
        return active.stream().anyMatch(target -> target.position.equals(point));
    }

    List<TrailTarget> nearby(Point position) {
        return active.stream().filter(target -> target.position.distanceTo(position) <= 1)
            .sorted(Comparator.comparingInt((TrailTarget target) -> target.position.distanceTo(position))
                .thenComparing(target -> target.kind)).toList();
    }

    TrailAttack ambush(Point position, Player player, Companion companion) {
        for (Point bush : map.locationsOf('R')) {
            if (bush.distanceTo(position) <= 1) return ambushAt(bush, position, player, companion);
        }
        return null;
    }

    TrailAttack ambushAt(Point bush, Point position, Player player, Companion companion) {
        map.set(bush, 'r');
        active.add(new TrailTarget(bush, Kind.MONSTER));
        String message = companion.blockHit() ? "A bramble monster jumps out! tanhoohoo blocks the hit."
            : damage(player, 3, "A bramble monster jumps out!");
        return new TrailAttack(bush, position, TrailAttack.Kind.CREATURE, message);
    }

    Strike strike(Point position, TrailTarget target, Player player, Companion companion) {
        if (!nearby(position).contains(target)) return new Strike(null, "Move next to the target.");
        Weapon weapon = player.arsenal.equipped();
        if (weapon == null) return new Strike(null, "Buy a weapon at ARMORY in Home Land first.");
        String message = weapon.label + " hits " + target.kind.label + " for " + weapon.damage + ". ";
        if (target.hit(weapon)) {
            remove(target);
            if (target.kind == Kind.WILD_COW) {
                player.backpack.add(Item.RAW_BEEF, 2);
                message += "Hunted a wild beef cow. Collected 2 cuts of raw beef.";
            } else {
                player.earn(3);
                message += "Monster defeated. Coins +3.";
            }
        } else {
            message += target.view().health() + "/" + target.kind.maxHealth + " HP left.";
            if (target.counters()) {
                message += companion.blockHit() ? " tanhoohoo blocks the counterattack."
                    : " " + damage(player, 1, "The monster strikes back!");
            }
        }
        return new Strike(target.position, message);
    }

    boolean canLeadHome(Point position, TrailTarget target) {
        return nearby(position).contains(target) && target.canLeadHome();
    }

    void leadAway(TrailTarget target) {
        remove(target);
    }

    private void remove(TrailTarget target) {
        active.remove(target);
        if (target.kind == Kind.WILD_COW) map.set(target.position, '.');
    }

    private static String damage(Player player, int amount, String message) {
        player.takeDamage(amount);
        return message + " HP -" + amount + ".";
    }
}
