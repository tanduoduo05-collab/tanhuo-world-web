package tanhuo;

import java.util.List;
import java.util.stream.Collectors;

final class Kitchen {
    final List<Recipe> recipes = List.of(
        new Recipe("Cook luosifen", Item.LUOSIFEN, List.of(Item.RICE_NOODLES, Item.SOUR_BAMBOO)),
        new Recipe("Fry an egg", Item.FRIED_EGG, List.of(Item.EGG)),
        new Recipe("Add fried egg to luosifen", Item.EGG_LUOSIFEN, List.of(Item.LUOSIFEN, Item.FRIED_EGG)),
        new Recipe("Pan-sear beef", Item.PAN_SEARED_BEEF, List.of(Item.RAW_BEEF))
    );

    private int bambooInJar;
    private int bambooReady;

    Item dishAt(char counter) {
        return switch (counter) {
            case 'L' -> Item.LUOSIFEN;
            case 'F' -> Item.FRIED_EGG;
            case 'E' -> Item.EGG_LUOSIFEN;
            case 'U' -> Item.PAN_SEARED_BEEF;
            default -> null;
        };
    }

    String recipes() {
        return recipes.stream().map(Recipe::description).collect(Collectors.joining("\n"));
    }

    String startFermenting(Backpack bag) {
        if (!bag.take(Item.BAMBOO_SHOOT)) return "Dig up a bamboo shoot along the Wildflower Trail.";
        bambooInJar++;
        return "Bamboo shoot added to the jar. Rest until tomorrow to finish fermenting.";
    }

    String collect(Backpack bag) {
        if (bambooReady == 0) {
            return bambooInJar > 0 ? "Bamboo is fermenting. Check again tomorrow." : "The fermentation jar is empty.";
        }
        bag.add(Item.SOUR_BAMBOO, bambooReady);
        bambooReady = 0;
        return "Collected pickled bamboo from the fermentation jar.";
    }

    void nextDay() {
        bambooReady += bambooInJar;
        bambooInJar = 0;
    }
}
