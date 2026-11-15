package tanhuo;

final class Player {
    static final String NAME = "tanduoduo";
    static final int MAX_HEALTH = 10;
    static final int MAX_STAMINA = 15;

    final Backpack backpack = new Backpack();
    final Arsenal arsenal = new Arsenal();
    private int health = MAX_HEALTH;
    private int stamina = MAX_STAMINA;
    private int coins = 6;

    int health() {
        return health;
    }

    int stamina() {
        return stamina;
    }

    int coins() {
        return coins;
    }

    boolean spendStamina(int cost) {
        if (stamina < cost) return false;
        stamina -= cost;
        return true;
    }

    boolean pay(int price) {
        if (coins < price) return false;
        coins -= price;
        return true;
    }

    void earn(int amount) {
        coins += amount;
    }

    void takeDamage(int amount) {
        health = Math.max(0, health - amount);
    }

    void recoverAfterRescue() {
        health = 1;
    }

    void rest() {
        health = MAX_HEALTH;
        stamina = MAX_STAMINA;
    }

    String eat(Item item) {
        if (!item.isFood()) return "You can't eat that item.";
        int nextHealth = Math.min(MAX_HEALTH, health + item.health);
        int nextStamina = Math.min(MAX_STAMINA, stamina + item.stamina);
        if (nextHealth == health && nextStamina == stamina) {
            return "Eating " + item.label + " now would not restore HP or SP.";
        }
        if (!backpack.take(item)) return "You have none in your backpack: " + item.label + ".";
        int restoredHealth = nextHealth - health;
        int restoredStamina = nextStamina - stamina;
        health = nextHealth;
        stamina = nextStamina;
        return "You ate " + item.label + ". HP +" + restoredHealth + ", SP +" + restoredStamina + ".";
    }
}
