package tanhuo;

final class GardenWeather {
    enum Night {
        NORMAL("Watered crops grow 1 step."),
        SOAK("Rain waters crops: +2 growth."),
        FLOOD("Waterlogging: no growth."),
        SNOW("Snow: no growth tonight."),
        FROST("Hard frost: harvest ripe crops!"),
        FREEZE("Warning: crops will freeze.");

        final String forecast;

        Night(String forecast) {
            this.forecast = forecast;
        }
    }

    private int rainyNights;
    private boolean lastNightHeavySnow;

    Night forecast(WeatherMode weather) {
        return switch (weather) {
            case RAIN -> rainyNights >= 2 ? Night.FLOOD : Night.SOAK;
            case HEAVY_RAIN -> Night.FLOOD;
            case SNOW -> Night.SNOW;
            case HEAVY_SNOW -> lastNightHeavySnow ? Night.FREEZE : Night.FROST;
            default -> Night.NORMAL;
        };
    }

    Night advance(WeatherMode weather) {
        Night night = forecast(weather);
        rainyNights = weather.isRain() ? Math.min(2, rainyNights + 1) : 0;
        lastNightHeavySnow = weather == WeatherMode.HEAVY_SNOW;
        return night;
    }
}
