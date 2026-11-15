package tanhuo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import tanhuo.Grid.Point;

final class Farmyard {
    record AnimalView(Point previous, Point position, FarmAnimal.Kind kind, boolean facingLeft) {}

    static final int BEEF_COW_LIMIT = 4;

    private static final List<Point> DIRECTIONS = List.of(
        new Point(0, -1), new Point(1, 0), new Point(0, 1), new Point(-1, 0));

    private static final class Resident {
        final FarmAnimal animal;
        final int number;
        Point position;
        Point previous;
        Point lastTile;
        boolean facingLeft;
        int pauseTicks = ThreadLocalRandom.current().nextInt(5);

        Resident(FarmAnimal animal, int number, Point position) {
            this.animal = animal;
            this.number = number;
            this.position = position;
            previous = position;
            lastTile = position;
        }
    }

    private final List<Resident> animals = new ArrayList<>(List.of(
        new Resident(new Chicken(), 1, new Point(5, 7)),
        new Resident(new Chicken(), 2, new Point(7, 8)),
        new Resident(new Cow(), 1, new Point(9, 7)),
        new Resident(new Cow(), 2, new Point(11, 8)),
        new Resident(new BeefCow(), 1, new Point(13, 8))));
    private int nextBeefCowNumber = 2;

    List<AnimalView> view() {
        return animals.stream().map(animal -> new AnimalView(
            animal.previous, animal.position, animal.animal.kind(), animal.facingLeft)).toList();
    }

    boolean occupied(Point point) {
        return animals.stream().anyMatch(animal -> animal.position.equals(point));
    }

    long beefCowCount() {
        return animals.stream().filter(resident -> resident.animal.kind() == FarmAnimal.Kind.BEEF_COW).count();
    }

    boolean adoptBeefCow(Grid map, Point companion) {
        if (beefCowCount() >= BEEF_COW_LIMIT) return false;
        Point spot = map.locationsOf('.').stream()
            .filter(point -> point.y() >= 7 && !occupied(point) && !point.equals(companion))
            .findFirst().orElse(null);
        if (spot == null) return false;
        animals.add(new Resident(new BeefCow(), nextBeefCowNumber++, spot));
        return true;
    }

    void addChoices(List<Game.Choice> choices, Point player, Backpack backpack) {
        for (Resident resident : animals) {
            if (resident.position.distanceTo(player) <= 1) {
                choices.add(new Game.Choice(resident.animal.actionLabel(resident.number),
                    () -> collect(resident, backpack)));
            }
        }
    }

    private String collect(Resident resident, Backpack backpack) {
        String result = resident.animal.collect(backpack);
        if (!resident.animal.isPresent()) animals.remove(resident);
        return result;
    }

    void wander(Grid map, Point player, Point companion) {
        var random = ThreadLocalRandom.current();
        for (Resident resident : animals) {
            resident.previous = resident.position;
            if (resident.position.distanceTo(player) <= 1) continue;
            if (resident.pauseTicks > 0) {
                resident.pauseTicks--;
                continue;
            }
            List<Point> directions = new ArrayList<>(DIRECTIONS);
            Collections.shuffle(directions);
            Point back = new Point(resident.lastTile.x() - resident.position.x(),
                resident.lastTile.y() - resident.position.y());
            if (directions.remove(back)) directions.add(back);
            for (Point direction : directions) {
                Point next = resident.position.offset(direction.x(), direction.y());
                if (map.at(next) != '.' || next.equals(player) || next.equals(companion)
                        || animals.stream().anyMatch(other ->
                            other.position.equals(next) || other.previous.equals(next))) continue;
                resident.lastTile = resident.position;
                resident.position = next;
                if (direction.x() != 0) resident.facingLeft = direction.x() < 0;
                break;
            }
            resident.pauseTicks = resident.animal.kind() == FarmAnimal.Kind.CHICKEN
                ? random.nextInt(1, 5) : random.nextInt(3, 8);
        }
    }

    void nextDay() {
        animals.forEach(resident -> resident.animal.nextDay());
    }
}
