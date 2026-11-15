package tanhuo;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

final class WeatherPanel extends JPanel {
    private enum Option {
        OFF("Off", WeatherMode.CLEAR), LIVE("Live weather", null),
        RAIN("Preview rain", WeatherMode.RAIN), DOWNPOUR("Preview heavy rain", WeatherMode.HEAVY_RAIN),
        SNOW("Preview snow", WeatherMode.SNOW), BLIZZARD("Preview heavy snow", WeatherMode.HEAVY_SNOW);

        final String label;
        final WeatherMode weather;

        Option(String label, WeatherMode weather) {
            this.label = label;
            this.weather = weather;
        }

        @Override
        public String toString() {
            return label;
        }
    }
    private final OpenMeteo api = new OpenMeteo();
    private final Consumer<WeatherMode> display;
    private final JTextField latitude = new JTextField("43.0618", 7);
    private final JTextField longitude = new JTextField("141.3545", 7);
    private final JComboBox<Option> mode = new JComboBox<>(Option.values());
    private final JLabel status = new JLabel("Weather off");
    private final JLabel garden = label("");
    private final Timer refresh = new Timer(15 * 60_000, event -> fetch());
    private OpenMeteo.Coordinates active;
    private CompletableFuture<WeatherMode> pending;
    private WeatherMode lastLive;
    private int requestNumber;

    WeatherPanel(Consumer<WeatherMode> display) {
        super(new BorderLayout(0, 4));
        this.display = display;
        setOpaque(false);
        JPanel coordinates = new JPanel(new GridLayout(2, 2, 4, 2));
        coordinates.setOpaque(false);
        coordinates.add(label("Latitude"));
        coordinates.add(label("Longitude"));
        latitude.setFont(PixelFont.size(13));
        longitude.setFont(PixelFont.size(13));
        latitude.getAccessibleContext().setAccessibleName("Latitude");
        longitude.getAccessibleContext().setAccessibleName("Longitude");
        coordinates.add(latitude);
        coordinates.add(longitude);

        JPanel options = new JPanel(new BorderLayout(5, 0));
        options.setOpaque(false);
        mode.setFont(PixelFont.size(13));
        mode.getAccessibleContext().setAccessibleName("Weather mode");
        JButton apply = new JButton("Apply");
        apply.setFont(PixelFont.size(13));
        apply.addActionListener(event -> apply());
        latitude.addActionListener(event -> apply());
        longitude.addActionListener(event -> apply());
        options.add(mode, BorderLayout.CENTER);
        options.add(apply, BorderLayout.EAST);

        JPanel fields = new JPanel(new BorderLayout(0, 4));
        fields.setOpaque(false);
        fields.add(coordinates, BorderLayout.NORTH);
        fields.add(options, BorderLayout.SOUTH);
        status.setFont(PixelFont.size(12));
        JLabel source = label("Weather data: Open-Meteo");
        source.setToolTipText("https://open-meteo.com/");
        JPanel notes = new JPanel(new GridLayout(3, 1, 0, 1));
        notes.setOpaque(false);
        notes.add(status);
        notes.add(garden);
        notes.add(source);
        add(fields, BorderLayout.NORTH);
        add(notes, BorderLayout.SOUTH);
    }

    void gardenForecast(GardenWeather.Night night) {
        garden.setText(night.forecast);
    }

    private static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(PixelFont.size(12));
        return label;
    }

    private void apply() {
        Option selected = (Option) mode.getSelectedItem();
        OpenMeteo.Coordinates requested = null;
        if (selected == Option.LIVE) {
            try {
                requested = new OpenMeteo.Coordinates(Double.parseDouble(latitude.getText().strip()),
                    Double.parseDouble(longitude.getText().strip()));
            } catch (IllegalArgumentException exception) {
                status.setText("Check latitude / longitude.");
                status.setToolTipText("Use decimal degrees: latitude -90 to 90, longitude -180 to 180.");
                return;
            }
        }
        stop();
        if (selected == Option.LIVE) {
            if (!requested.equals(active)) {
                lastLive = null;
                display.accept(WeatherMode.CLEAR);
            }
            active = requested;
            fetch();
            refresh.restart();
            return;
        }
        active = null;
        lastLive = null;
        WeatherMode preview = selected.weather;
        display.accept(preview);
        status.setText(selected == Option.OFF ? "Weather off" : "Preview: " + preview.label);
        status.setToolTipText(null);
    }

    private void fetch() {
        if (active == null) return;
        int request = ++requestNumber;
        status.setText("Syncing weather...");
        status.setToolTipText(null);
        pending = api.current(active);
        pending.whenComplete((weather, error) -> SwingUtilities.invokeLater(() -> {
            if (request != requestNumber) return;
            pending = null;
            if (error != null) {
                status.setText(lastLive == null ? "Weather sync failed" : "Last sync: " + lastLive.label);
                status.setToolTipText("Could not reach Open-Meteo. Apply to retry.");
                return;
            }
            lastLive = weather;
            display.accept(weather);
            status.setText("Live: " + weather.label);
        }));
    }

    void stop() {
        refresh.stop();
        requestNumber++;
        if (pending != null) pending.cancel(true);
        pending = null;
    }
}
