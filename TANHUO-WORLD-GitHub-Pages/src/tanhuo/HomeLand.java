package tanhuo;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import tanhuo.Grid.Point;

final class HomeLand {
    static final Point START = new Point(2, 4);
    static final Point COMPANION_HOME = new Point(6, 4);
    static final Point GATE = new Point(15, 6);
    static final Point BRAZIER = new Point(5, 1);

    final Grid map = new Grid(
        "#################",
        "#....B...-KLFEJU#",
        "#.12345..|...Y.|#",
        "#........---.---#",
        "#...............#",
        "#...............#",
        "#..............G#",
        "#...............#",
        "#...............#",
        "#.MW............#",
        "#################"
    );
    final Merchant merchant = new Merchant();
    final WeaponShop weaponShop = new WeaponShop();
    final Kitchen kitchen = new Kitchen();
    final DryingRack dryingRack = new DryingRack();
    final Farmyard farmyard = new Farmyard();
    private final Map<Character, Plot> plots = Map.of(
        '1', new Plot(PlantType.WATERMELON),
        '2', new Plot(),
        '3', new Plot(),
        '4', new Plot(),
        '5', new Plot()
    );
    private final Map<Point, Plot> tilledPlots = new LinkedHashMap<>();
    private final GardenWeather gardenWeather = new GardenWeather();
    private int day = 1;
    private boolean fireLit;

    int day() {
        return day;
    }

    boolean isFireLit() {
        return fireLit;
    }

    Plot plotAt(Point point) {
        return map.at(point) == 'P' ? tilledPlots.get(point) : plots.get(map.at(point));
    }

    String till(Point point, Player player) {
        if (map.at(point) != '.' || farmyard.occupied(point)) return "Choose an empty grass tile.";
        if (!player.spendStamina(2)) return "Tilling requires 2 SP.";
        tilledPlots.put(point, new Plot());
        map.set(point, 'P');
        return "Tilled a new plot. SP -2. You can plant seeds here.";
    }

    List<String> gardenStatus() {
        List<String> status = new ArrayList<>();
        for (char number = '1'; number <= '5'; number++) {
            status.add("Plot " + number + ": " + plots.get(number).description());
        }
        tilledPlots.forEach((point, plot) ->
            status.add("Tilled plot (" + point.x() + ", " + point.y() + "): " + plot.description()));
        return status;
    }

    String lightFire(Backpack backpack) {
        if (!backpack.take(Item.CHARCOAL)) return "The brazier is still cold. You can find charcoal in Outside World.";
        fireLit = true;
        return "tanduoduo lights the brazier. The room slowly warms up.";
    }

    GardenWeather.Night gardenForecast(WeatherMode weather) {
        return gardenWeather.forecast(weather);
    }

    String rest(Player player, Companion companion, WeatherMode weather) {
        GardenWeather.Night night = gardenWeather.advance(weather);
        int frozen = 0;
        for (Plot plot : plots.values()) if (plot.nextDay(night)) frozen++;
        for (Plot plot : tilledPlots.values()) if (plot.nextDay(night)) frozen++;
        kitchen.nextDay();
        dryingRack.nextDay();
        farmyard.nextDay();
        player.rest();
        companion.rest();
        day++;
        String rest = fireLit ? "You rest together by the fire. By morning, the embers have gone out."
            : "You both rest through the night. Your HP and SP are restored.";
        fireLit = false;
        return rest + switch (night) {
            case NORMAL -> "";
            case SOAK -> "\nRain watered the garden and gave growing crops 2 growth steps.";
            case FLOOD -> "\nThe garden was waterlogged. Crops did not grow.";
            case SNOW -> "\nSnow paused crop growth for the night.";
            case FROST -> "\nCrops survived the first night of heavy snow, but did not grow. Another heavy snow night will freeze them.";
            case FREEZE -> "\n" + frozen + " crop(s) froze, including any left ripe. Clear frozen plants to replant.";
        };
    }
}
