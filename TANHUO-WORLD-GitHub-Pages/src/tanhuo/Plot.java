package tanhuo;

final class Plot {
    private PlantType plant;
    private int daysRemaining;
    private boolean watered;
    private boolean frozen;

    Plot() {
    }

    Plot(PlantType maturePlant) {
        plant = maturePlant;
    }

    boolean isEmpty() {
        return plant == null && !frozen;
    }

    boolean isMature() {
        return plant != null && daysRemaining == 0;
    }

    boolean canWater() {
        return plant != null && !isMature() && !watered;
    }

    Item product() {
        return plant == null ? null : plant.product;
    }

    boolean isWatered() {
        return watered;
    }

    boolean isFrozen() {
        return frozen;
    }

    String clearFrozenPlant() {
        if (!frozen) return "There is no frozen plant to clear.";
        frozen = false;
        return "Cleared the frozen plant. The plot is ready for a new seed.";
    }

    String plant(PlantType type, Backpack backpack) {
        if (!isEmpty()) return "There's already a plant in this plot.";
        if (!backpack.take(type.seed)) return "You have none in your backpack: " + type.seed.label + ".";
        plant = type;
        daysRemaining = type.daysToMature;
        return "Planted: " + type.seed.label + ". Water it and rest to advance its growth by one day.";
    }

    String water() {
        if (!canWater()) return "This plot doesn't need watering right now.";
        watered = true;
        return "Watered: " + plant.product.label + ".";
    }

    String harvest(Backpack backpack) {
        if (!isMature()) return "This plot isn't ready to harvest yet.";
        PlantType harvest = plant;
        backpack.add(harvest.product, harvest.yield);
        if (harvest.regrows) {
            daysRemaining = harvest.daysToRegrow;
        } else {
            plant = null;
        }
        if (harvest == PlantType.HEART) {
            return "tanduoduo and tanhoohoo harvest their first heart together.";
        }
        return "Harvested " + harvest.yield + " x " + harvest.product.label + ".";
    }

    boolean nextDay(GardenWeather.Night weather) {
        if (plant != null && weather == GardenWeather.Night.FREEZE) {
            plant = null;
            daysRemaining = 0;
            watered = false;
            frozen = true;
            return true;
        }
        int growth = switch (weather) {
            case NORMAL -> watered ? 1 : 0;
            case SOAK -> 2;
            default -> 0;
        };
        daysRemaining = Math.max(0, daysRemaining - growth);
        watered = false;
        return false;
    }

    String description() {
        if (frozen) return "Frozen plant (clear it to replant)";
        if (isEmpty()) return "Empty plot";
        if (isMature()) return plant.product.label + " is ready to harvest";
        String waterStatus = watered ? "watered today" : "needs water today";
        return plant.product.label + ": " + daysRemaining + " growth step(s) to maturity, " + waterStatus;
    }
}
