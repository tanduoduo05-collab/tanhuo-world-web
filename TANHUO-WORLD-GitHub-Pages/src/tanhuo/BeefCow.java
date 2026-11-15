package tanhuo;

final class BeefCow extends FarmAnimal {
    private boolean present = true;

    @Override
    Kind kind() {
        return Kind.BEEF_COW;
    }

    @Override
    String actionLabel(int number) {
        return "Slaughter beef cow #" + number;
    }

    @Override
    boolean isPresent() {
        return present;
    }

    @Override
    String collect(Backpack bag) {
        if (!present) return "The beef cow is gone.";
        present = false;
        bag.add(Item.RAW_BEEF, 2);
        return "Collected 2 cuts of raw beef.";
    }

    @Override
    void nextDay() {
        // Beef cows do not produce anything overnight.
    }
}
