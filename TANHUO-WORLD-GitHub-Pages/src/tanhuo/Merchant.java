package tanhuo;

import java.util.List;

final class Merchant {
    record Offer(Item item, int price) {}

    final List<Offer> stock = List.of(
        new Offer(Item.WATERMELON_SEED, 5),
        new Offer(Item.STRAWBERRY_SEED, 2),
        new Offer(Item.GRAPE_SEED, 3),
        new Offer(Item.ORANGE_SEED, 4),
        new Offer(Item.RICE_NOODLES, 2)
    );

    final List<Offer> buyback = List.of(
        new Offer(Item.WATERMELON, 4),
        new Offer(Item.STRAWBERRY, 1),
        new Offer(Item.GRAPES, 2),
        new Offer(Item.ORANGE, 3)
    );

    String buy(Player player, Offer offer) {
        if (!stock.contains(offer)) return "The merchant doesn't sell that item.";
        if (!player.pay(offer.price())) return "You don't have enough coins.";
        player.backpack.add(offer.item(), 1);
        return "Paid " + offer.price() + " coins for: " + offer.item().label + ".";
    }

    String sell(Player player, Offer offer) {
        if (!buyback.contains(offer)) return "The merchant doesn't buy that item.";
        if (!player.backpack.take(offer.item())) return "You have none to sell: " + offer.item().label + ".";
        player.earn(offer.price());
        return "Sold: " + offer.item().label + " for " + offer.price() + " coin(s).";
    }
}
