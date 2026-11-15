package tanhuo;

import java.util.EnumMap;

final class Backpack {
    private final EnumMap<Item, Integer> quantities = new EnumMap<>(Item.class);

    int count(Item item) {
        return quantities.getOrDefault(item, 0);
    }

    void add(Item item, int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        quantities.merge(item, amount, Integer::sum);
    }

    boolean take(Item item) {
        int amount = count(item);
        if (amount == 0) return false;
        if (amount == 1) {
            quantities.remove(item);
        } else {
            quantities.put(item, amount - 1);
        }
        return true;
    }
}
