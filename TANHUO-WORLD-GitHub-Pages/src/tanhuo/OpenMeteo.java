package tanhuo;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

final class OpenMeteo {
    record Coordinates(double latitude, double longitude) {
        Coordinates {
            if (!Double.isFinite(latitude) || Math.abs(latitude) > 90
                    || !Double.isFinite(longitude) || Math.abs(longitude) > 180) {
                throw new IllegalArgumentException("Coordinates out of range");
            }
        }
    }

    private static final Pattern CURRENT = Pattern.compile("\"current\"\\s*:\\s*\\{([^{}]*)}");
    private static final Pattern WEATHER_CODE = Pattern.compile("\"weather_code\"\\s*:\\s*(\\d+)\\s*(?:,|$)");
    CompletableFuture<WeatherMode> current(Coordinates location) {
        URI url = URI.create("https://api.open-meteo.com/v1/forecast?latitude=" + location.latitude()
            + "&longitude=" + location.longitude() + "&current=weather_code");
        return CompletableFuture.supplyAsync(() -> fetch(url));
    }

    private WeatherMode fetch(URI url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.toURL().openConnection();
            connection.setConnectTimeout(7000);
            connection.setReadTimeout(7000);
            int status = connection.getResponseCode();
            if (status != 200) throw new IOException("Weather service returned HTTP " + status);
            try (var body = connection.getInputStream()) {
                return parse(new String(body.readAllBytes(), StandardCharsets.UTF_8));
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    static WeatherMode parse(String json) {
        var current = CURRENT.matcher(json);
        if (current.find()) {
            var code = WEATHER_CODE.matcher(current.group(1));
            if (code.find()) return WeatherMode.fromCode(Integer.parseInt(code.group(1)));
        }
        throw new IllegalArgumentException("Weather response did not contain a current code");
    }
}
