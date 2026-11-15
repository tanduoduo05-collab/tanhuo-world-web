package tanhuo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;
import tanhuo.Grid.Point;

final class WorldPanel extends JPanel {
    private static final int TILE = 32;
    private final Game game;
    private final WeatherEffect weatherEffect = new WeatherEffect();
    private final long weatherClock = System.nanoTime();
    private final Timer weatherFrames;
    private WeatherMode weather = WeatherMode.CLEAR;
    private BufferedImage pixels;
    private boolean facingLeft;
    private int step;
    private Game.AttackAnimation flight;
    private Game.MeleeAnimation swing;
    private long launchedAt;
    private long swungAt;
    private long homeMovedAt;
    private static final double WALK_NANOS = 360_000_000.0;

    WorldPanel(Game game) {
        this.game = game;
        weatherFrames = new Timer(33, event -> {
            if (isShowing() && !game.isExploring()) repaint();
        });
        setBackground(new Color(0x243b32));
        setPreferredSize(new Dimension(780, 500));
    }

    void setWeather(WeatherMode mode) {
        weather = mode;
        if (mode.isRain() || mode.isSnow()) weatherFrames.start();
        else weatherFrames.stop();
        repaint();
    }

    void stopWeather() {
        weatherFrames.stop();
    }

    void walked(int dx) {
        if (dx != 0) facingLeft = dx < 0;
        step++;
    }

    void animateAttack(Game.AttackAnimation animation, Runnable finished) {
        flight = animation;
        launchedAt = System.nanoTime();
        Timer frames = new Timer(16, event -> {
            if (flightProgress() >= 1.0) {
                ((Timer) event.getSource()).stop();
                flight = null;
                finished.run();
            }
            repaint();
        });
        frames.start();
        repaint();
    }

    void animateHomeMovement() {
        homeMovedAt = System.nanoTime();
        Timer frames = new Timer(16, event -> {
            if (walkProgress() >= 1.0) ((Timer) event.getSource()).stop();
            repaint();
        });
        frames.start();
    }

    void animateMelee(Game.MeleeAnimation animation, Runnable finished) {
        swing = animation;
        swungAt = System.nanoTime();
        Timer frames = new Timer(16, event -> {
            if (swingProgress() >= 1.0) {
                ((Timer) event.getSource()).stop();
                swing = null;
                finished.run();
            }
            repaint();
        });
        frames.start();
        repaint();
    }

    private double swingProgress() {
        return (System.nanoTime() - swungAt) / 240_000_000.0;
    }

    private double walkProgress() {
        return Math.min(1.0, (System.nanoTime() - homeMovedAt) / WALK_NANOS);
    }

    private double flightProgress() {
        int duration = flight.attack().kind() == TrailAttack.Kind.ARROW ? 340 : 560;
        return (System.nanoTime() - launchedAt) / (duration * 1_000_000.0);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Game.Scene scene = flight != null ? flight.scene() : swing != null ? swing.scene() : game.scene();
        int width = scene.terrain().get(0).length() * TILE;
        int height = scene.terrain().size() * TILE;
        if (pixels == null || pixels.getWidth() != width || pixels.getHeight() != height) {
            pixels = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D map = pixels.createGraphics();
        for (int y = 0; y < scene.terrain().size(); y++) {
            String row = scene.terrain().get(y);
            for (int x = 0; x < row.length(); x++) {
                Point point = new Point(x, y);
                map.translate(x * TILE, y * TILE);
                PixelArt.tile(map, row.charAt(x), x, y, scene.outside(), scene.fireLit(),
                    scene.outside() ? null : game.plotView(point));
                Item dish = scene.outside() ? null : game.displayedDish(row.charAt(x));
                if (dish != null) PixelArt.servedMeal(map, dish);
                if (row.charAt(x) == 'Y') PixelArt.dryingRack(map, game.hangingBeef());
                map.translate(-x * TILE, -y * TILE);
            }
        }
        for (TrailTarget.View target : scene.targets()) drawTarget(map, target);
        double progress = walkProgress();
        double eased = progress * progress * (3 - 2 * progress);
        for (Farmyard.AnimalView animal : scene.animals()) {
            Graphics2D sprite = (Graphics2D) map.create();
            int x = (int) Math.round((animal.position().x() * eased
                + animal.previous().x() * (1 - eased)) * TILE);
            int y = (int) Math.round((animal.position().y() * eased
                + animal.previous().y() * (1 - eased)) * TILE);
            boolean chicken = animal.kind() == FarmAnimal.Kind.CHICKEN;
            int bounce = animal.position().equals(animal.previous()) ? 0
                : (int) Math.round(Math.sin(Math.PI * progress) * (chicken ? 2 : 1));
            sprite.translate(x, y - bounce);
            if (animal.facingLeft()) {
                sprite.translate(TILE, 0);
                sprite.scale(-1, 1);
            }
            switch (animal.kind()) {
                case CHICKEN -> PixelArt.chicken(sprite);
                case DAIRY_COW -> PixelArt.cow(sprite);
                case BEEF_COW -> PixelArt.beefCow(sprite);
            }
            sprite.dispose();
        }
        if (scene.companion() != null) {
            Point partner = scene.companion();
            Point previous = scene.companionPrevious();
            int offset = partner.equals(scene.player()) ? 13 : 0;
            int x = (int) Math.round((partner.x() * eased + previous.x() * (1 - eased)) * TILE);
            int y = (int) Math.round((partner.y() * eased + previous.y() * (1 - eased)) * TILE);
            int stride = !partner.equals(previous) && progress > 0.25 && progress < 0.75 ? 1 : 0;
            drawPerson(map, x + offset, y, true, scene.companionFacingLeft(), stride);
        }
        char playerTile = scene.terrain().get(scene.player().y()).charAt(scene.player().x());
        int playerOffset = playerTile == 'W' ? 13 : "BMKJY".indexOf(playerTile) >= 0 ? -12 : 0;
        int playerX = scene.player().x() * TILE + playerOffset;
        int playerY = scene.player().y() * TILE;
        int aimX = swing == null ? 0 : swing.target().x() - scene.player().x();
        boolean leftFacing = aimX == 0 ? facingLeft : aimX < 0;
        drawPerson(map, playerX, playerY, false, leftFacing, step);
        if (scene.weapon() != null) drawHeldWeapon(map, scene, playerX, playerY, leftFacing);
        if (swing != null) drawHit(map);
        if (flight != null) drawAttack(map);
        if (!scene.outside()) {
            weatherEffect.paint(map, weather, width, height, (System.nanoTime() - weatherClock) / 1_000_000_000.0);
        }
        map.dispose();

        double scale = Math.min((getWidth() - 24.0) / width, (getHeight() - 24.0) / height);
        if (scale <= 0) return;
        int drawnWidth = (int) (width * scale);
        int drawnHeight = (int) (height * scale);
        int left = (getWidth() - drawnWidth) / 2;
        int top = (getHeight() - drawnHeight) / 2;
        Graphics2D canvas = (Graphics2D) graphics.create();
        canvas.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        canvas.setColor(new Color(0xd9b981));
        canvas.fillRect(left - 3, top - 3, drawnWidth + 6, drawnHeight + 6);
        canvas.drawImage(pixels, left, top, left + drawnWidth, top + drawnHeight, 0, 0, width, height, null);
        canvas.dispose();
    }

    private void drawTarget(Graphics2D map, TrailTarget.View target) {
        boolean monster = target.kind() == TrailTarget.Kind.MONSTER;
        if (monster && flight != null && flight.attack().kind() == TrailAttack.Kind.CREATURE
                && flight.attack().origin().equals(target.position())) return;
        Graphics2D sprite = (Graphics2D) map.create();
        double progress = walkProgress();
        Point from = target.previous();
        Point to = target.position();
        int x = (int) Math.round((from.x() + (to.x() - from.x()) * progress) * TILE);
        int y = (int) Math.round((from.y() + (to.y() - from.y()) * progress) * TILE);
        sprite.translate(x, y);

        if (monster) {
            sprite.translate(16, 19);
            PixelArt.creature(sprite);
            sprite.translate(-16, -19);
        } else {
            Graphics2D cow = (Graphics2D) sprite.create();
            if (target.facingLeft()) {
                cow.translate(TILE, 0);
                cow.scale(-1, 1);
            }
            PixelArt.beefCow(cow);
            cow.dispose();
        }
        int max = target.kind().maxHealth;
        sprite.setColor(new Color(0x422e40));
        sprite.fillRect(3, 1, 26, 4);
        sprite.setColor(new Color(monster ? 0xe5859f : 0xf2ad63));
        sprite.fillRect(4, 2, 24 * target.health() / max, 2);
        sprite.setFont(PixelFont.size(8));
        String value = target.health() + "/" + max;
        int textX = (TILE - sprite.getFontMetrics().stringWidth(value)) / 2;
        sprite.setColor(new Color(0x322635));
        sprite.drawString(value, textX + 1, 12);
        sprite.setColor(new Color(0xfff0d2));
        sprite.drawString(value, textX, 11);
        sprite.dispose();
    }

    private void drawHeldWeapon(Graphics2D map, Game.Scene scene, int x, int y, boolean left) {
        Graphics2D held = (Graphics2D) map.create();
        held.translate(x + (left ? 7 : 25), y + 20);
        if (swing != null) {
            int dx = swing.target().x() - scene.player().x();
            int dy = swing.target().y() - scene.player().y();
            double direction = dx == 0 && dy == 0 ? (left ? Math.PI : 0) : Math.atan2(dy, dx);
            held.rotate(direction + (swingProgress() - 0.5) * 1.7);
            held.translate(Math.sin(swingProgress() * Math.PI) * 6, 0);
            PixelArt.weapon(held, swing.weapon());
        } else {
            if (left) held.scale(-1, 1);
            held.rotate(-0.85);
            held.scale(0.8, 0.8);
            PixelArt.weapon(held, scene.weapon());
        }
        held.dispose();
    }

    private void drawHit(Graphics2D map) {
        if (swingProgress() < 0.25 || swingProgress() > 0.85) return;
        int x = swing.target().x() * TILE + 16;
        int y = swing.target().y() * TILE + 18;
        map.setColor(new Color(0xffe2a0));
        map.drawLine(x - 5, y + 5, x + 5, y - 5);
        map.fillRect(x + 6, y - 7, 2, 2);
        map.fillRect(x - 8, y + 5, 2, 2);
        map.setColor(new Color(0xfffaf0));
        map.drawLine(x - 3, y + 2, x + 4, y - 5);
    }

    private void drawAttack(Graphics2D map) {
        Point origin = flight.attack().origin();
        Point target = flight.attack().target();
        int dx = target.x() - origin.x();
        int dy = target.y() - origin.y();
        double progress = Math.min(1.0, flightProgress());
        boolean arrow = flight.attack().kind() == TrailAttack.Kind.ARROW;
        double travel = arrow ? 0.22 + 0.78 * progress : 1.0 - Math.abs(2.0 * progress - 1.0);
        double distance = TILE * travel;

        Graphics2D projectile = (Graphics2D) map.create();
        projectile.translate(origin.x() * TILE + TILE / 2.0 + distance * dx,
            origin.y() * TILE + TILE / 2.0 + distance * dy - (arrow ? 0 : Math.sin(Math.PI * travel) * 13));
        if (arrow) {
            projectile.rotate(Math.atan2(dy, dx));
            PixelArt.arrow(projectile);
        } else {
            PixelArt.creature(projectile);
        }
        projectile.dispose();
    }

    private void drawPerson(Graphics2D map, int x, int y, boolean partner, boolean left, int stride) {
        Graphics2D sprite = (Graphics2D) map.create();
        sprite.translate(x, y);
        PixelArt.person(sprite, partner, left, stride);
        sprite.dispose();
    }
}
