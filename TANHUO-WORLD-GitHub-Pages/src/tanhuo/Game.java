package tanhuo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import tanhuo.Grid.Point;

final class Game {
    record Choice(String label, Supplier<String> action) {}
    record PlotView(Item fruit, boolean mature, boolean watered, boolean frozen) {}
    record Scene(List<String> terrain, Point player, Point companion, boolean outside,
                 boolean fireLit, int health, int stamina, int coins, String protection,
                 List<Farmyard.AnimalView> animals, Point companionPrevious, boolean companionFacingLeft,
                 List<TrailTarget.View> targets, Weapon weapon) {}
    record AttackAnimation(Scene scene, TrailAttack attack) {}
    record MeleeAnimation(Scene scene, Point target, Weapon weapon) {}

    private final Player player = new Player();
    private final Companion companion = new Companion();
    private final HomeLand home = new HomeLand();
    private final HomeCompanion homeCompanion = new HomeCompanion();
    private final OutsideWorld outside;
    private AttackAnimation attackAnimation;
    private MeleeAnimation meleeAnimation;
    private WeatherMode weather = WeatherMode.CLEAR;
    private boolean birthdayCard;
    private Point position = HomeLand.START;
    private Point followingPosition;
    private boolean exploring;

    Game() {
        this(new OutsideWorld());
    }

    Game(OutsideWorld outside) {
        this.outside = outside;
    }

    String heading() {
        String location = exploring ? "Outside World - Wildflower Trail" : "Home Land";
        return "Day " + home.day() + " | " + location;
    }

    String status() {
        return "%s  HP %d/%d  SP %d/%d  Coins %d | %s: %s".formatted(
            Player.NAME, player.health(), Player.MAX_HEALTH, player.stamina(),
            Player.MAX_STAMINA, player.coins(), Companion.NAME, companion.status());
    }

    List<String> mapView() {
        return currentMap().render(position, companionPosition());
    }

    Scene scene() {
        Point partner = companionPosition();
        boolean roaming = !exploring && !companion.isInvited();
        return new Scene(currentMap().render(null, null), position, partner, exploring,
            home.isFireLit(), player.health(), player.stamina(), player.coins(), companion.status(),
            exploring ? List.of() : home.farmyard.view(),
            roaming ? homeCompanion.previous() : partner, roaming && homeCompanion.facingLeft(),
            exploring ? outside.encounters.view() : List.of(), exploring ? player.arsenal.equipped() : null);
    }

    boolean isExploring() {
        return exploring;
    }

    PlotView plotView(Point point) {
        Plot plot = home.plotAt(point);
        return plot == null ? null : new PlotView(plot.product(), plot.isMature(), plot.isWatered(), plot.isFrozen());
    }

    int itemCount(Item item) {
        return player.backpack.count(item);
    }

    Item displayedDish(char counter) {
        Item dish = home.kitchen.dishAt(counter);
        return dish != null && itemCount(dish) > 0 ? dish : null;
    }

    Item hangingBeef() {
        return home.dryingRack.hangingBeef();
    }

    AttackAnimation attackAnimation() {
        return attackAnimation;
    }

    MeleeAnimation takeMeleeAnimation() {
        MeleeAnimation animation = meleeAnimation;
        meleeAnimation = null;
        return animation;
    }

    boolean takeBirthdayCard() {
        boolean show = birthdayCard;
        birthdayCard = false;
        return show;
    }

    void setWeather(WeatherMode mode) {
        weather = mode;
    }

    GardenWeather.Night gardenForecast() {
        return home.gardenForecast(weather);
    }

    boolean owns(Weapon weapon) {
        return player.arsenal.owns(weapon);
    }

    Weapon equippedWeapon() {
        return player.arsenal.equipped();
    }

    long beefCowCount() {
        return home.farmyard.beefCowCount();
    }

    String tickWorld() {
        if (exploring) {
            outside.encounters.moveCows(position, companionPosition());
            return "";
        }
        String event = companion.isInvited() ? "" : homeCompanion.tick(home, position);
        home.farmyard.wander(home.map, position, companionPosition());
        return event;
    }

    String move(int dx, int dy) {
        attackAnimation = null;
        Point previous = position;
        Point next = position.offset(dx, dy);
        char tile = currentMap().at(next);
        if ("#-|LFEU".indexOf(tile) >= 0) return "You can't go that way.";
        if (tile == 'O') return "The stone blocks the path.";
        if (tile == '^') return "Thorns block the path. Clear them or go around.";
        if (exploring && outside.encounters.occupied(next)) return "Stop next to the target to interact.";
        if (exploring && tile == 'R') {
            TrailAttack ambush = outside.encounters.ambushAt(next, position, player, companion);
            attackAnimation = new AttackAnimation(scene(), ambush);
            return rescueIfNeeded(ambush.message());
        }
        if (companion.isInvited()) followingPosition = position;
        position = next;
        if (!exploring) {
            if (!position.equals(HomeLand.BRAZIER)) homeCompanion.leaveFire();
            return "";
        }

        TrailAttack attack = outside.approach(previous, position, player, companion);
        String event = attack == null ? "" : attack.message();
        if (attack != null) attackAnimation = new AttackAnimation(scene(), attack);
        if (player.health() > 0) {
            if (tile == 'Q') birthdayCard = true;
            String encounter = outside.stepOn(position, player);
            if (!encounter.isEmpty()) event = event.isEmpty() ? encounter : event + "\n" + encounter;
        }
        return rescueIfNeeded(event);
    }

    String attackNearest() {
        if (!exploring) return "Weapons can only be used in Outside World.";
        List<TrailTarget> targets = outside.encounters.nearby(position);
        return targets.isEmpty() ? "Move next to a creature or wild beef cow first." : strike(targets.get(0));
    }

    private String strike(TrailTarget target) {
        if (!exploring) return "Weapons can only be used in Outside World.";
        Scene before = scene();
        TrailEncounters.Strike result = outside.encounters.strike(position, target, player, companion);
        if (result.target() != null) meleeAnimation = new MeleeAnimation(before, result.target(), before.weapon());
        return rescueIfNeeded(result.message());
    }

    private String leadCowHome(TrailTarget target) {
        if (!exploring || !outside.encounters.canLeadHome(position, target)) {
            return "Weaken a wild beef cow to 1 or 2 HP before leading it home.";
        }
        if (home.farmyard.beefCowCount() >= Farmyard.BEEF_COW_LIMIT) {
            return "Your farmyard already has " + Farmyard.BEEF_COW_LIMIT + " beef cows.";
        }
        if (!home.farmyard.adoptBeefCow(home.map, homeCompanion.position())) {
            return "The farmyard needs an open grass tile for a new cow.";
        }
        outside.encounters.leadAway(target);
        return "You led the wild cow to your farmyard. Beef cows: "
            + home.farmyard.beefCowCount() + "/" + Farmyard.BEEF_COW_LIMIT + ".";
    }

    private String rescueIfNeeded(String event) {
        if (player.health() > 0) return event;
        player.recoverAfterRescue();
        arriveHome();
        return event + "\n" + (companion.isInvited()
            ? "tanhoohoo brought tanduoduo back to Home Land."
            : "tanduoduo made it back to Home Land, where tanhoohoo met them at the gate.")
            + " Rest by the brazier to recover.";
    }

    List<Choice> choices() {
        List<Choice> choices = new ArrayList<>();
        if (exploring) {
            addOutsideChoices(choices);
        } else {
            addHomeChoices(choices);
        }
        for (Weapon weapon : Weapon.values()) {
            if (owns(weapon) && equippedWeapon() != weapon) {
                choices.add(new Choice("Equip " + weapon.label, () -> player.arsenal.equip(weapon)));
            }
        }
        for (Item item : Item.values()) {
            if (item.isFood() && player.backpack.count(item) > 0) {
                String verb = item == Item.MILK ? "Drink " : "Eat ";
                choices.add(new Choice(verb + item.label + " (" + item.recovery() + ")",
                    () -> player.eat(item)));
            }
        }
        choices.add(new Choice("Check backpack", this::backpackContents));
        return choices;
    }

    private void addHomeChoices(List<Choice> choices) {
        if (!companion.isInvited() && distanceTo(companionPosition()) <= 1) {
            choices.add(new Choice("Invite tanhoohoo", this::inviteCompanion));
        }
        if (position.equals(HomeLand.GATE)) {
            choices.add(new Choice("Go to Outside World", this::depart));
        }

        char tile = home.map.at(position);
        if (tile == 'B') {
            if (home.isFireLit()) {
                choices.add(new Choice("Warm up with tanhoohoo", this::warmUp));
            } else {
                String label = companion.isInvited() ? "Light the fire with tanhoohoo" : "Light the fire";
                choices.add(new Choice(label, this::lightFire));
            }
            choices.add(new Choice("Rest until tomorrow", this::rest));
        }
        if (tile == 'M') addTradeChoices(choices);
        if (tile == 'W') {
            for (Weapon weapon : Weapon.values()) {
                if (!owns(weapon)) {
                    choices.add(new Choice("Buy " + weapon.label + " (" + weapon.price
                        + " coins, " + weapon.damage + " damage)", () -> home.weaponShop.buy(player, weapon)));
                }
            }
        }
        if (tile == 'K' || home.map.at(position.offset(0, -1)) == 'K') {
            choices.add(new Choice("View recipes", home.kitchen::recipes));
            for (Recipe recipe : home.kitchen.recipes) {
                choices.add(new Choice(recipe.name(), () -> recipe.cook(player.backpack)));
            }
        }
        if (tile == 'J') {
            choices.add(new Choice("Ferment a bamboo shoot", () -> home.kitchen.startFermenting(player.backpack)));
            choices.add(new Choice("Check fermentation jar", () -> home.kitchen.collect(player.backpack)));
        }
        if (tile == 'Y') {
            choices.add(new Choice("Hang raw beef", () -> home.dryingRack.hang(player.backpack)));
            choices.add(new Choice("Check drying rack", () -> home.dryingRack.collect(player.backpack)));
        }
        home.farmyard.addChoices(choices, position, player.backpack);
        if (tile == '.' && !home.farmyard.occupied(position)) {
            choices.add(new Choice("Till this tile (2 SP)", () -> home.till(position, player)));
        }

        Plot plot = home.plotAt(position);
        if (plot != null) addGardenChoices(choices, plot);
    }

    private void addOutsideChoices(List<Choice> choices) {
        Weapon equipped = equippedWeapon();
        for (TrailTarget target : outside.encounters.nearby(position)) {
            if (equipped != null) {
                choices.add(new Choice("Attack " + target.kind.label + " (" + target.view().health()
                    + "/" + target.kind.maxHealth + " HP)", () -> strike(target)));
            } else {
                choices.add(new Choice("Inspect " + target.kind.label,
                    () -> target.kind.label + ": " + target.view().health() + "/" + target.kind.maxHealth
                        + " HP. Buy a weapon at ARMORY in Home Land."));
            }
            if (target.canLeadHome()) {
                choices.add(new Choice("Lead wild beef cow home", () -> leadCowHome(target)));
            }
        }
        if (outside.map.at(position) == 'D') {
            choices.add(new Choice("Dig up bamboo shoot", () -> outside.digBamboo(position, player.backpack)));
        }
        if (position.equals(OutsideWorld.GATE)) {
            choices.add(new Choice("Return to Home Land", this::arriveHome));
        }
        for (Point thorns : outside.map.locationsOf('^')) {
            if (distanceTo(thorns) == 1) {
                choices.add(new Choice("Clear nearby thorns (2 SP)", () -> outside.clearThorns(thorns, player)));
            }
        }
    }

    private void addTradeChoices(List<Choice> choices) {
        for (Merchant.Offer offer : home.merchant.stock) {
            choices.add(new Choice("Buy " + offer.item().label + " (" + offer.price() + " coins)",
                () -> home.merchant.buy(player, offer)));
        }
        for (Merchant.Offer offer : home.merchant.buyback) {
            if (player.backpack.count(offer.item()) > 0) {
                choices.add(new Choice("Sell " + offer.item().label + " (+" + offer.price() + " coins)",
                    () -> home.merchant.sell(player, offer)));
            }
        }
    }

    private void addGardenChoices(List<Choice> choices, Plot plot) {
        if (plot.isFrozen()) choices.add(new Choice("Clear frozen plant", plot::clearFrozenPlant));
        if (plot.isEmpty()) {
            for (PlantType plant : PlantType.values()) {
                if (player.backpack.count(plant.seed) > 0) {
                    choices.add(new Choice("Plant " + plant.seed.label, () -> plot.plant(plant, player.backpack)));
                }
            }
        }
        if (plot.canWater() && !weather.isRain()) choices.add(new Choice("Water", plot::water));
        if (plot.isMature()) {
            choices.add(new Choice("Harvest", () -> plot.harvest(player.backpack)));
        }
        choices.add(new Choice("Check garden plots", () -> String.join("\n", home.gardenStatus())));
    }

    private String backpackContents() {
        List<String> contents = new ArrayList<>();
        for (Item item : Item.values()) {
            int count = player.backpack.count(item);
            if (count > 0) contents.add(item.label + " x" + count);
        }
        for (Weapon weapon : Weapon.values()) {
            if (owns(weapon)) contents.add(weapon.label + (equippedWeapon() == weapon ? " (equipped)" : ""));
        }
        return contents.isEmpty() ? "Your backpack is empty." : String.join(", ", contents);
    }

    private String rest() {
        homeCompanion.leaveFire();
        outside.nextDay();
        return home.rest(player, companion, weather) + "\nThe Wildflower Trail has changed overnight.";
    }

    private String depart() {
        homeCompanion.leaveFire();
        exploring = true;
        position = OutsideWorld.GATE;
        followingPosition = companion.isInvited() ? position.offset(0, -1) : null;
        return companion.isInvited() ? "You and tanhoohoo set out along the Wildflower Trail." : "tanduoduo sets out along the Wildflower Trail alone.";
    }

    private String arriveHome() {
        exploring = false;
        position = HomeLand.GATE;
        followingPosition = companion.isInvited() ? position.offset(-1, 0) : null;
        return "You returned to Home Land.";
    }

    private Point companionPosition() {
        if (companion.isInvited()) return followingPosition;
        return exploring ? null : homeCompanion.position();
    }

    private String inviteCompanion() {
        followingPosition = homeCompanion.position();
        homeCompanion.leaveFire();
        return companion.invite();
    }

    private String lightFire() {
        String event = home.lightFire(player.backpack);
        if (home.isFireLit() && companion.isInvited()) {
            return "tanduoduo adds charcoal, and tanhoohoo lights the kindling.";
        }
        return event;
    }

    private String warmUp() {
        return companion.isInvited()
            ? "tanduoduo and tanhoohoo warm their hands together."
            : homeCompanion.warmUp();
    }

    private int distanceTo(Point target) {
        return position.distanceTo(target);
    }

    private Grid currentMap() {
        return exploring ? outside.map : home.map;
    }
}
