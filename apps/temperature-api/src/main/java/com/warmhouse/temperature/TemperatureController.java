package com.warmhouse.temperature;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

import static java.util.concurrent.ThreadLocalRandom.current;

@RestController
public class TemperatureController {

    private static final Map<String, String> DEFAULT_LOCATIONS = Map.of(
            "1", "Living Room",
            "2", "Bedroom",
            "3", "Kitchen"
    );

    @GetMapping("/temperature")
    public TemperatureResponse getTemperature(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sensorId) {
        String resolvedLocation = resolveLocation(location, sensorId);
        String resolvedSensorId = resolveSensorId(sensorId, resolvedLocation);
        return randomTemperature(resolvedLocation, resolvedSensorId);
    }

    @GetMapping("/temperature/{sensorId}")
    public TemperatureResponse getTemperatureBySensorId(@PathVariable String sensorId) {
        String location = resolveLocation(null, sensorId);
        return randomTemperature(location, sensorId);
    }

    private TemperatureResponse randomTemperature(String location, String sensorId) {
        double value = Math.round(current().nextDouble(18.0, 28.0) * 10.0) / 10.0;
        return new TemperatureResponse(
                value,
                "°C",
                Instant.now(),
                location,
                "active",
                sensorId,
                "temperature",
                "Random temperature for " + location
        );
    }

    private String resolveLocation(String location, String sensorId) {
        if (location != null && !location.isBlank()) {
            return location;
        }
        return DEFAULT_LOCATIONS.getOrDefault(sensorId, "Unknown");
    }

    private String resolveSensorId(String sensorId, String location) {
        if (sensorId != null && !sensorId.isBlank()) {
            return sensorId;
        }
        return DEFAULT_LOCATIONS.entrySet().stream()
                .filter(entry -> entry.getValue().equalsIgnoreCase(location))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("0");
    }

    public record TemperatureResponse(
            double value,
            String unit,
            Instant timestamp,
            String location,
            String status,
            String sensor_id,
            String sensor_type,
            String description
    ) {
    }
}
