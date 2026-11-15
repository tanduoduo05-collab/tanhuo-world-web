package tanhuo;

enum Item {
    WATERMELON("watermelon", 2, 1),
    STRAWBERRY("strawberry", 1, 0),
    GRAPES("grapes", 0, 2),
    ORANGE("orange", 3, 0),
    WATERMELON_SEED("watermelon seed"),
    STRAWBERRY_SEED("strawberry seed"),
    GRAPE_SEED("grape seed"),
    ORANGE_SEED("orange seed"),
    HEART_SEED("heart seed"),
    HEART("heart"),
    CHARCOAL("charcoal"),
    RICE_NOODLES("rice noodles"),
    BAMBOO_SHOOT("bamboo shoot"),
    SOUR_BAMBOO("pickled bamboo shoot"),
    EGG("egg"),
    MILK("milk", 0, 1),
    FRIED_EGG("fried egg", 1, 0),
    LUOSIFEN("luosifen", 3, 2),
    EGG_LUOSIFEN("luosifen with fried egg", 4, 2),
    RAW_BEEF("raw beef"),
    BEEF_JERKY("beef jerky", 2, 4),
    PAN_SEARED_BEEF("pan-seared beef", 5, 2),
    BIRTHDAY_CAKE("birthday cake");

    final String label;
    final int health;
    final int stamina;

    Item(String label) {
        this(label, 0, 0);
    }

    Item(String label, int health, int stamina) {
        this.label = label;
        this.health = health;
        this.stamina = stamina;
    }

    boolean isFood() {
        return health > 0 || stamina > 0;
    }

    String recovery() {
        if (health == 0) return "SP +" + stamina;
        if (stamina == 0) return "HP +" + health;
        return "HP +" + health + ", SP +" + stamina;
    }
}
