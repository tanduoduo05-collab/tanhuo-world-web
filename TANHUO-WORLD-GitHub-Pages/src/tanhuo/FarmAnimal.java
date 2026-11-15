package tanhuo;

abstract class FarmAnimal {
    enum Kind { CHICKEN, DAIRY_COW, BEEF_COW }

    abstract Kind kind();
    abstract String actionLabel(int number);
    abstract String collect(Backpack bag);
    abstract void nextDay();

    boolean isPresent() {
        return true;
    }
}
