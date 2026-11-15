package tanhuo;

abstract class DailyProducer extends FarmAnimal {
    private boolean ready = true;

    protected abstract Item product();

    final String collect(Backpack bag) {
        Item item = product();
        if (!ready) {
            return "Check again tomorrow for more " + item.label + ".";
        }
        bag.add(item, 1);
        ready = false;
        return "Collected: " + item.label + ".";
    }

    final void nextDay() {
        ready = true;
    }
}

