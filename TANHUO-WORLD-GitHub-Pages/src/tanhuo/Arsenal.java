package tanhuo;

import java.util.EnumSet;

final class Arsenal {
    private final EnumSet<Weapon> owned = EnumSet.noneOf(Weapon.class);
    private Weapon equipped;

    boolean owns(Weapon weapon) {
        return owned.contains(weapon);
    }

    Weapon equipped() {
        return equipped;
    }

    void acquire(Weapon weapon) {
        owned.add(weapon);
        if (equipped == null) equipped = weapon;
    }

    String equip(Weapon weapon) {
        if (!owns(weapon)) return "You don't own that weapon.";
        equipped = weapon;
        return "Equipped: " + weapon.label + ".";
    }
}
