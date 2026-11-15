package tanhuo;

enum PlantType {
    WATERMELON(Item.WATERMELON_SEED, Item.WATERMELON, 3, 2, 1, true),
    STRAWBERRY(Item.STRAWBERRY_SEED, Item.STRAWBERRY, 1, 1, 3, true),
    GRAPE(Item.GRAPE_SEED, Item.GRAPES, 2, 1, 2, true),
    ORANGE(Item.ORANGE_SEED, Item.ORANGE, 3, 2, 2, true),
    HEART(Item.HEART_SEED, Item.HEART, 2, 0, 1, false);

    final Item seed;
    final Item product;
    final int daysToMature;
    final int daysToRegrow;
    final int yield;
    final boolean regrows;

    PlantType(Item seed, Item product, int daysToMature, int daysToRegrow, int yield, boolean regrows) {
        this.seed = seed;
        this.product = product;
        this.daysToMature = daysToMature;
        this.daysToRegrow = daysToRegrow;
        this.yield = yield;
        this.regrows = regrows;
    }
}
