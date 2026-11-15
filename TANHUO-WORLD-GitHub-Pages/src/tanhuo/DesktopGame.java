package tanhuo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

final class DesktopGame {
    private static final Color PAPER = new Color(0xffefd1);
    private static final Color INK = new Color(0x322b30);
    private static final Color WOOD = new Color(0xf0d4a7);
    private final Game game;
    private final boolean web;
    private final WorldPanel world;
    private final WeatherPanel weather;
    private final JPanel root = new JPanel(new BorderLayout());
    private final JLabel location = label(17);
    private final JLabel status = label(16);
    private final JLabel partner = label(13);
    private final JProgressBar health = meter(Player.MAX_HEALTH, new Color(0xc4554e));
    private final JProgressBar stamina = meter(Player.MAX_STAMINA, new Color(0x5e8b50));
    private final JPanel actions = new JPanel();
    private final JPanel inventory = new JPanel(new GridLayout(0, 1, 0, 3));
    private final JTextArea message = new JTextArea(4, 24);
    private List<Game.Choice> choices = List.of();
    private boolean attackAnimating;
    private boolean cardOpen;

    DesktopGame(Game game) {
        this(game, false);
    }

    DesktopGame(Game game, boolean web) {
        this.game = game;
        this.web = web;
        world = new WorldPanel(game);
        weather = new WeatherPanel(this::setWeather);
        buildLayout();
        bindMovement();
        refresh("The garden watermelon is ripe. Invite tanhoohoo and bring back charcoal for the brazier.");
    }

    void open() {
        open(0, 0);
    }

    void open(int width, int height) {
        JFrame frame = new JFrame("TANHUO WORLD");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(root);
        if (web) {
            frame.setUndecorated(true);
            frame.setBounds(0, 0, width, height);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            frame.pack();
            frame.setMinimumSize(new Dimension(780, 590));
            frame.setLocationRelativeTo(null);
        }
        Timer homeClock = new Timer(420, event -> {
            if (!attackAnimating && !cardOpen) {
                String message = game.tickWorld();
                updateNearbyActions();
                if (!message.isEmpty()) showMessage(message);
                world.animateHomeMovement();
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                homeClock.stop();
                weather.stop();
                world.stopWeather();
            }
        });
        frame.setVisible(true);
        homeClock.start();
    }

    private void buildLayout() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PAPER);
        header.setBorder(web ? BorderFactory.createEmptyBorder(4, 8, 4, 8)
            : BorderFactory.createEmptyBorder(12, 18, 12, 18));
        JPanel title = new JPanel(new GridLayout(0, 1, 0, 3));
        title.setOpaque(false);
        JLabel name = label(web ? 19 : 36);
        name.setText("TANHUO WORLD");
        title.add(name);
        title.add(location);
        header.add(title, BorderLayout.WEST);
        JPanel stats = new JPanel(new GridLayout(0, 1, 0, 3));
        stats.setOpaque(false);
        JPanel meters = new JPanel(new GridLayout(1, 0, 8, 0));
        meters.setOpaque(false);
        meters.add(health);
        meters.add(stamina);
        meters.add(status);
        stats.add(meters);
        stats.add(partner);
        header.add(stats, BorderLayout.EAST);
        if (web) {
            location.setFont(PixelFont.size(12));
            partner.setFont(PixelFont.size(11));
        }

        JPanel sidebar = new JPanel(new BorderLayout(0, 12));
        sidebar.setBackground(PAPER);
        sidebar.setPreferredSize(new Dimension(260, 500));
        sidebar.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setBackground(PAPER);
        JScrollPane actionScroll = new JScrollPane(actions);
        actionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        actionScroll.setBorder(BorderFactory.createEmptyBorder());
        actionScroll.getVerticalScrollBar().setUnitIncrement(14);
        inventory.setBackground(PAPER);
        JScrollPane bagScroll = new JScrollPane(inventory);
        bagScroll.setBorder(BorderFactory.createEmptyBorder());
        bagScroll.setPreferredSize(new Dimension(220, 125));
        if (web) {
            JTabbedPane tabs = new JTabbedPane();
            tabs.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            tabs.setFocusable(false);
            tabs.addTab("Actions", actionScroll);
            tabs.addTab("Bag", bagScroll);
            JPanel weatherTab = new JPanel(new BorderLayout());
            weatherTab.setBorder(BorderFactory.createEmptyBorder(5, 4, 5, 4));
            weatherTab.add(weather, BorderLayout.NORTH);
            tabs.addTab("Weather", weatherTab);
            sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            sidebar.setPreferredSize(new Dimension(218, 220));
            sidebar.add(tabs, BorderLayout.CENTER);
            message.setRows(2);
        } else {
            sidebar.add(section("Actions", actionScroll), BorderLayout.CENTER);
            sidebar.add(section("Weather", weather), BorderLayout.NORTH);
            sidebar.add(section("Backpack", bagScroll), BorderLayout.SOUTH);
        }

        message.setEditable(false);
        message.setFocusable(false);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setForeground(INK);
        message.setBackground(WOOD);
        message.setFont(PixelFont.size(14));
        message.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(WOOD);
        if (web) {
            footer.setBorder(BorderFactory.createEmptyBorder(1, 4, 4, 4));
            JScrollPane log = new JScrollPane(message);
            log.setBorder(BorderFactory.createEmptyBorder());
            footer.add(log, BorderLayout.NORTH);
            footer.add(touchControls(), BorderLayout.CENTER);
        } else {
            footer.setBorder(BorderFactory.createEmptyBorder(9, 18, 5, 18));
            JLabel controls = label(13);
            controls.setText("WASD / Arrow keys: Move     F: Attack nearby     E / 1-9: Actions");
            footer.add(controls, BorderLayout.NORTH);
            footer.add(message, BorderLayout.CENTER);
        }
        root.add(header, BorderLayout.NORTH);
        root.add(world, BorderLayout.CENTER);
        root.add(sidebar, BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);
    }

    private JPanel touchControls() {
        JPanel controls = new JPanel(new GridLayout(1, 6, 5, 0));
        controls.setOpaque(false);
        addTouchButton(controls, "\u2190", "Move left", () -> move(-1, 0));
        addTouchButton(controls, "\u2191", "Move up", () -> move(0, -1));
        addTouchButton(controls, "\u2193", "Move down", () -> move(0, 1));
        addTouchButton(controls, "\u2192", "Move right", () -> move(1, 0));
        addTouchButton(controls, "Action", "Use the first available action", () -> select(0));
        addTouchButton(controls, "Attack", "Attack a nearby target", this::attack);
        return controls;
    }

    private void addTouchButton(JPanel controls, String text, String description, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, text.length() == 1 ? 23 : 15));
        button.setPreferredSize(new Dimension(50, 45));
        button.setFocusable(false);
        button.getAccessibleContext().setAccessibleName(description);
        button.addActionListener(event -> action.run());
        controls.add(button);
    }

    private void attack() {
        if (!attackAnimating && !cardOpen) finishAction(game.attackNearest());
    }

    private JPanel section(String title, JComponent content) {
        JPanel section = new JPanel(new BorderLayout(0, 6));
        section.setOpaque(false);
        JLabel heading = label(17);
        heading.setText(title);
        section.add(heading, BorderLayout.NORTH);
        section.add(content, BorderLayout.CENTER);
        return section;
    }

    private void bindMovement() {
        bind("W", () -> move(0, -1));
        bind("UP", () -> move(0, -1));
        bind("A", () -> move(-1, 0));
        bind("LEFT", () -> move(-1, 0));
        bind("S", () -> move(0, 1));
        bind("DOWN", () -> move(0, 1));
        bind("D", () -> move(1, 0));
        bind("RIGHT", () -> move(1, 0));
        bind("E", () -> select(0));
        bind("F", this::attack);
        for (int i = 0; i < 9; i++) {
            int index = i;
            bind(Integer.toString(i + 1), () -> select(index));
        }
    }

    private void bind(String key, Runnable task) {
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
        root.getActionMap().put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                task.run();
            }
        });
    }

    private void move(int dx, int dy) {
        if (attackAnimating || cardOpen) return;
        Game.Scene before = game.scene();
        String event = game.move(dx, dy);
        if (!before.player().equals(game.scene().player())) world.walked(dx);
        Game.AttackAnimation attack = game.attackAnimation();
        if (attack == null) {
            finishAction(event);
            return;
        }
        attackAnimating = true;
        world.animateAttack(attack, () -> {
            attackAnimating = false;
            finishAction(event);
        });
    }

    private void select(int index) {
        if (!attackAnimating && !cardOpen && index < choices.size()) {
            finishAction(choices.get(index).action().get());
        }
    }

    private void finishAction(String event) {
        Game.MeleeAnimation melee = game.takeMeleeAnimation();
        if (melee != null) {
            attackAnimating = true;
            world.animateMelee(melee, () -> {
                attackAnimating = false;
                refresh(event);
            });
            return;
        }
        refresh(event);
        if (game.takeBirthdayCard()) {
            cardOpen = true;
            try {
                BirthdayCard.show(world);
            } finally {
                cardOpen = false;
            }
        }
    }

    private void setWeather(WeatherMode mode) {
        game.setWeather(mode);
        world.setWeather(mode);
        weather.gardenForecast(game.gardenForecast());
        updateNearbyActions();
    }

    private void refresh(String event) {
        Game.Scene scene = game.scene();
        location.setText(game.heading());
        health.setValue(scene.health());
        health.setString("HP " + scene.health() + "/" + Player.MAX_HEALTH);
        stamina.setValue(scene.stamina());
        stamina.setString("SP " + scene.stamina() + "/" + Player.MAX_STAMINA);
        status.setText("Coins " + scene.coins());
        partner.setText("tanhoohoo: " + scene.protection()
            + (scene.outside() ? "" : " | Beef cows " + game.beefCowCount() + "/" + Farmyard.BEEF_COW_LIMIT));
        weather.gardenForecast(game.gardenForecast());
        if (!event.isEmpty()) showMessage(event);
        refreshActions(game.choices());
        refreshInventory();
        world.repaint();
    }

    private void showMessage(String event) {
        message.setText(event);
        message.setCaretPosition(0);
    }

    private void updateNearbyActions() {
        List<Game.Choice> updated = game.choices();
        if (!choices.stream().map(Game.Choice::label).toList()
                .equals(updated.stream().map(Game.Choice::label).toList())) {
            refreshActions(updated);
        } else {
            choices = updated;
        }
    }

    private void refreshActions(List<Game.Choice> updated) {
        choices = updated;
        actions.removeAll();
        for (int i = 0; i < choices.size(); i++) {
            int index = i;
            String prefix = i < 9 ? (i + 1) + ". " : "";
            String caption = prefix + choices.get(i).label();
            JButton button = new JButton("<html><div style='width:166px; font-family:Tanhuo Pixel'>"
                + caption + "</div></html>");
            button.getAccessibleContext().setAccessibleName(caption);
            button.setFont(PixelFont.size(13));
            button.setHorizontalAlignment(JButton.LEFT);
            button.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            button.setFocusable(false);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            button.setBackground(WOOD);
            button.setForeground(INK);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xc69d6d)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
            button.addActionListener(event -> select(index));
            actions.add(button);
            actions.add(Box.createVerticalStrut(5));
        }
        actions.revalidate();
        actions.repaint();
    }

    private void refreshInventory() {
        inventory.removeAll();
        for (Weapon weapon : Weapon.values()) {
            if (!game.owns(weapon)) continue;
            JLabel row = label(13);
            row.setText(weapon.label + (game.equippedWeapon() == weapon ? " (equipped)" : ""));
            BufferedImage icon = new BufferedImage(26, 24, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = icon.createGraphics();
            graphics.translate(6, 12);
            PixelArt.weapon(graphics, weapon);
            graphics.dispose();
            row.setIcon(new ImageIcon(icon));
            inventory.add(row);
        }
        for (Item item : Item.values()) {
            int count = game.itemCount(item);
            if (count == 0) continue;
            JLabel row = label(13);
            row.setText(item.label + " x" + count);
            BufferedImage icon = new BufferedImage(26, 24, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = icon.createGraphics();
            PixelArt.fruit(graphics, item, 3, 2);
            graphics.dispose();
            row.setIcon(new ImageIcon(icon));
            inventory.add(row);
        }
        if (inventory.getComponentCount() == 0) {
            JLabel empty = label(13);
            empty.setText("Empty");
            inventory.add(empty);
        }
        inventory.revalidate();
        inventory.repaint();
    }

    private static JLabel label(int size) {
        JLabel label = new JLabel();
        label.setFont(PixelFont.size(size));
        label.setForeground(INK);
        return label;
    }

    private static JProgressBar meter(int maximum, Color color) {
        JProgressBar meter = new JProgressBar(0, maximum);
        meter.setStringPainted(true);
        meter.setForeground(color);
        meter.setBackground(new Color(0xe7cba1));
        meter.setFont(PixelFont.size(14));
        return meter;
    }
}
