package tanhuo;

final class Cow extends DailyProducer {
    @Override
    Kind kind() {
        return Kind.DAIRY_COW;
    }

    @Override
    String actionLabel(int number) {
        return "Collect milk (dairy cow " + number + ")";
    }

    @Override
    protected Item product() {
        return Item.MILK;
    }
}
