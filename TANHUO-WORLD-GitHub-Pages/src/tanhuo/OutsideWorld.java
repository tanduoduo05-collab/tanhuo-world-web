package tanhuo;

import tanhuo.Grid.Point;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

final class OutsideWorld {
    static final Point GATE = new Point(1, 6);
    static final Point BIRTHDAY_CAKE = GATE.offset(1, 0);
    private final ArrowTrap arrowTrap;

    final Grid map = new Grid(
        "#####################",
        "#......#........#...#",
        "#..#...#..##....#...#",
        "#..#................#",
        "#.#...##.....#......#",
        "#.........#..#......#",
        "#G........#.....#...#",
        "#...#...........#...#",
        "#...#..#............#",
        "#......#......#.....#",
        "#.........#...#.....#",
        "#...................#",
        "#####################"
    );
    final TrailEncounters encounters = new TrailEncounters(map);
    private final TrailLayout layout;
    OutsideWorld() {
        this(new ArrowTrap(() -> ThreadLocalRandom.current().nextInt(100)));
    }

    OutsideWorld(ArrowTrap arrowTrap) {
        this(arrowTrap, new Random());
    }

    OutsideWorld(ArrowTrap arrowTrap, Random random) {
        this.arrowTrap = arrowTrap;
        layout = new TrailLayout(map, GATE, random);
        layout.nextDay();
        encounters.reset();
        map.set(BIRTHDAY_CAKE, 'Q');
    }

    TrailAttack approach(Point before, Point after, Player player, Companion companion) {
        for (Point stone : map.locationsOf('O')) {
            TrailAttack shot = arrowTrap.approach(stone, before, after, player, companion);
            if (shot != null) return shot;
        }
        return encounters.ambush(after, player, companion);
    }

    String stepOn(Point point, Player player) {
        char tile = map.at(point);
        return switch (tile) {
            case 'C' -> collect(point, player.backpack, Item.CHARCOAL);
            case 'S' -> collect(point, player.backpack, Item.HEART_SEED);
            case 'V' -> collect(point, player.backpack, Item.GRAPES);
            case 'Q' -> collect(point, player.backpack, Item.BIRTHDAY_CAKE);
            default -> "";
        };
    }

    String digBamboo(Point point, Backpack bag) {
        if (map.at(point) != 'D') return "There is no bamboo shoot here.";
        map.set(point, '.');
        bag.add(Item.BAMBOO_SHOOT, 1);
        return "You dug up a bamboo shoot. Bring it to the fermentation jar in Home Land.";
    }

    String clearThorns(Point point, Player player) {
        if (map.at(point) != '^') return "There are no thorns here.";
        if (!player.spendStamina(2)) return "Clearing thorns costs 2 SP. Eat some fruit or go around.";
        map.set(point, '.');
        return "You cleared the thorns. SP -2.";
    }

    void nextDay() {
        for (Point cow : map.locationsOf('H')) map.set(cow, '.');
        if (map.at(BIRTHDAY_CAKE) == 'Q') map.set(BIRTHDAY_CAKE, '.');
        layout.nextDay();
        encounters.reset();
    }

    private String collect(Point point, Backpack backpack, Item item) {
        map.set(point, '.');
        backpack.add(item, 1);
        return "Picked up: " + item.label + ".";
    }

}
