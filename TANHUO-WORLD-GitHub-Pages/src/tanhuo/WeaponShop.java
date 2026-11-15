package tanhuo;

final class WeaponShop {
    String buy(Player player, Weapon weapon) {
        if (player.arsenal.owns(weapon)) return "You already own the " + weapon.label + ".";
        if (!player.pay(weapon.price)) return "You don't have enough coins.";
        player.arsenal.acquire(weapon);
        return "Bought " + weapon.label + ". " + (player.arsenal.equipped() == weapon
            ? "Equipped." : "Select Equip " + weapon.label + " to switch.");
    }
}
