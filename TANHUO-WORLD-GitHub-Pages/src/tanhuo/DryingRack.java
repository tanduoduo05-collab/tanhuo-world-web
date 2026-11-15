package tanhuo;

final class DryingRack {
    private Item hangingBeef;

    Item hangingBeef() {
        return hangingBeef;
    }

    String hang(Backpack bag) {
        if (hangingBeef != null) return "The rack is already occupied.";
        if (!bag.take(Item.RAW_BEEF)) return "You need raw beef to use the drying rack.";
        hangingBeef = Item.RAW_BEEF;
        return "Raw beef is hanging on the rack. Rest overnight to dry it.";
    }

    String collect(Backpack bag) {
        if (hangingBeef == null) return "The drying rack is empty.";
        if (hangingBeef == Item.RAW_BEEF) return "The beef is still drying. Check again tomorrow.";
        bag.add(Item.BEEF_JERKY, 1);
        hangingBeef = null;
        return "Collected beef jerky (HP +2, SP +4).";
    }

    void nextDay() {
        if (hangingBeef == Item.RAW_BEEF) hangingBeef = Item.BEEF_JERKY;
    }
}
