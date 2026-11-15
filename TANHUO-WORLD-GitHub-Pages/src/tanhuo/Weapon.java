package tanhuo;

enum Weapon {
    KNIFE("Knife", 1, 4),
    AXE("Axe", 2, 9);

    final String label;
    final int damage;
    final int price;

    Weapon(String label, int damage, int price) {
        this.label = label;
        this.damage = damage;
        this.price = price;
    }
}
