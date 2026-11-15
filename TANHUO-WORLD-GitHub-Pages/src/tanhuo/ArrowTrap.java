package tanhuo;

import java.util.function.IntSupplier;
import tanhuo.Grid.Point;

final class ArrowTrap {
    private final IntSupplier roll;

    ArrowTrap(IntSupplier roll) {
        this.roll = roll;
    }

    TrailAttack approach(Point stone, Point before, Point after, Player player, Companion companion) {
        if (stone.distanceTo(before) <= 1 || stone.distanceTo(after) != 1 || roll.getAsInt() >= 35) {
            return null;
        }
        if (companion.blockHit()) {
            return new TrailAttack(stone, after, TrailAttack.Kind.ARROW,
                "An arrow shoots from the stone! tanhoohoo blocks it.");
        }
        player.takeDamage(2);
        return new TrailAttack(stone, after, TrailAttack.Kind.ARROW, "An arrow shoots from the stone! HP -2.");
    }
}
