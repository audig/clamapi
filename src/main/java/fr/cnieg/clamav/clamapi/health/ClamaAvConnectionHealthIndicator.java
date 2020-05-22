package fr.cnieg.clamav.clamapi.health;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import fr.cnieg.clamav.clamapi.services.ClamAVService;
import fr.cnieg.clamav.clamapi.services.ScanService;

@Component
public class ClamaAvConnectionHealthIndicator implements HealthIndicator {

    @Autowired
    private ClamAVService clamAVService;

    Logger logger = LoggerFactory.getLogger(ScanService.class);

    public Health health() {
        try {
            clamAVService.ping();
        } catch (IOException e) {
            logger.error("Failed to ping clamav: {}", e.getMessage());
            return Health.down().withDetail("ClamAv Connection down", e.getMessage()).build();
        }

        return Health.up().build();
    }
}
