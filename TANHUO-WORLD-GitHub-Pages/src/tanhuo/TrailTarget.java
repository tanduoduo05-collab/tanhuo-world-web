package tanhuo;

import tanhuo.Grid.Point;

final class TrailTarget {
    enum Kind {
        MONSTER("Bramble monster", 10),
        WILD_COW("Wild beef cow", 5);

        final String label;
        final int maxHealth;

        Kind(String label, int maxHealth) {
            this.label = label;
            this.maxHealth = maxHealth;
        }
    }

    record View(Point position, Kind kind, int health, Point previous, boolean facingLeft) {}

    Point position;
    private Point previous;
    private boolean facingLeft;
    final Kind kind;
    private int health;
    private int hitsSinceCounter;

    TrailTarget(Point position, Kind kind) {
        this.position = position;
        this.previous = position;
        this.kind = kind;
        health = kind.maxHealth;
    }

    View view() {
        return new View(position, kind, health, previous, facingLeft);
    }

    void moveTo(Point next) {
        previous = position;
        if (next.x() != position.x()) facingLeft = next.x() < position.x();
        position = next;
    }

    boolean hit(Weapon weapon) {
        health = Math.max(0, health - weapon.damage);
        return health == 0;
    }

    boolean canLeadHome() {
        return kind == Kind.WILD_COW && health > 0 && health <= 2;
    }

    boolean counters() {
        if (kind != Kind.MONSTER || ++hitsSinceCounter < 3) return false;
        hitsSinceCounter = 0;
        return true;
    }
}
