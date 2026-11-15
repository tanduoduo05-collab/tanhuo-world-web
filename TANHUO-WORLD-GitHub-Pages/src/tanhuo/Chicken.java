package tanhuo;

final class Chicken extends DailyProducer {
    @Override
    Kind kind() {
        return Kind.CHICKEN;
    }

    @Override
    String actionLabel(int number) {
        return "Collect egg (chicken " + number + ")";
    }

    @Override
    protected Item product() {
        return Item.EGG;
    }
}
