package tanhuo;

import java.util.List;
import java.util.stream.Collectors;

record Recipe(String name, Item result, List<Item> ingredients) {
    Recipe {
        ingredients = List.copyOf(ingredients);
    }

    String description() {
        return name + ": " + ingredients.stream().map(item -> item.label)
            .collect(Collectors.joining(" + "));
    }

    String cook(Backpack bag) {
        for (Item ingredient : ingredients) {
            long needed = ingredients.stream().filter(item -> item == ingredient).count();
            if (bag.count(ingredient) < needed) return "Recipe requires " + description() + ".";
        }
        ingredients.forEach(bag::take);
        bag.add(result, 1);
        return "Prepared: " + result.label + " (" + result.recovery() + ").";
    }
}
