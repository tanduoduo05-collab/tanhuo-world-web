package tanhuo;

enum WeatherMode {
    CLEAR("Clear"), CLOUDY("Cloudy"), FOG("Fog"),
    RAIN("Rain"), HEAVY_RAIN("Heavy rain"), SNOW("Snow"), HEAVY_SNOW("Heavy snow");

    final String label;

    WeatherMode(String label) {
        this.label = label;
    }

    boolean isRain() {
        return this == RAIN || this == HEAVY_RAIN;
    }

    boolean isSnow() {
        return this == SNOW || this == HEAVY_SNOW;
    }

    static WeatherMode fromCode(int code) {
        return switch (code) {
            case 0, 1 -> CLEAR;
            case 2, 3 -> CLOUDY;
            case 45, 48 -> FOG;
            case 51, 53, 56, 61, 63, 66, 80, 81 -> RAIN;
            case 55, 57, 65, 67, 82, 95, 96, 97, 99 -> HEAVY_RAIN;
            case 71, 73, 77, 85 -> SNOW;
            case 75, 86 -> HEAVY_SNOW;
            default -> throw new IllegalArgumentException("Unknown weather code: " + code);
        };
    }
}
