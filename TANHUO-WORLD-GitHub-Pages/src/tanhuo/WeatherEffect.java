package tanhuo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

final class WeatherEffect {
    private record Particle(double x, double y, double speed, int size) {}

    private final Particle[] particles = new Particle[230];

    WeatherEffect() {
        Random random = new Random(17);
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle(random.nextDouble(), random.nextDouble(),
                0.65 + random.nextDouble() * 0.7, 1 + random.nextInt(3));
        }
    }

    void paint(Graphics2D map, WeatherMode weather, int width, int height, double seconds) {
        switch (weather) {
            case CLEAR -> { }
            case CLOUDY -> wash(map, width, height, new Color(53, 65, 95, 27));
            case FOG -> wash(map, width, height, new Color(224, 231, 223, 72));
            case RAIN, HEAVY_RAIN -> rain(map, width, height, seconds, weather == WeatherMode.HEAVY_RAIN);
            case SNOW, HEAVY_SNOW -> snow(map, width, height, seconds, weather == WeatherMode.HEAVY_SNOW);
        }
    }

    private void rain(Graphics2D map, int width, int height, double time, boolean heavy) {
        wash(map, width, height, new Color(32, 54, 98, heavy ? 75 : 49));
        map.setColor(new Color(173, 213, 249, 172));
        int count = heavy ? particles.length : 120;
        for (int i = 0; i < count; i++) {
            Particle drop = particles[i];
            double travel = time * drop.speed() * (heavy ? 1.4 : 1.0);
            int x = wrap(drop.x() * width - travel * 56, width);
            int y = wrap(drop.y() * height + travel * 250, height);
            int length = (heavy ? 10 : 6) + 2 * drop.size();
            map.drawLine(x, y, x - 3, y + length);
        }
    }

    private void snow(Graphics2D map, int width, int height, double time, boolean heavy) {
        wash(map, width, height, new Color(205, 226, 242, heavy ? 92 : 45));
        map.setColor(new Color(251, 253, 255, 220));
        int count = heavy ? particles.length : 110;
        for (int i = 0; i < count; i++) {
            Particle flake = particles[i];
            double travel = time * flake.speed();
            int x = wrap(flake.x() * width + travel * (heavy ? 33 : 7) + Math.sin(travel * 1.8 + i) * 12, width);
            int y = wrap(flake.y() * height + travel * (heavy ? 60 : 32), height);
            map.fillRect(x, y, flake.size(), flake.size());
            if (flake.size() == 3) {
                map.drawLine(x + 1, y - 1, x + 1, y + 3);
                map.drawLine(x - 1, y + 1, x + 3, y + 1);
            }
        }
    }

    private static int wrap(double value, int limit) {
        return Math.floorMod((int) Math.floor(value), limit);
    }

    private static void wash(Graphics2D map, int width, int height, Color tint) {
        map.setColor(tint);
        map.fillRect(0, 0, width, height);
    }
}
