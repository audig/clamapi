package fr.cnieg.clamav.clamapi.configuration.health;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import fr.cnieg.clamav.clamapi.services.ScanService;

@Component
public class ClamaAvConnectionHealthIndicator implements HealthIndicator {

    @Autowired
    private ScanService scanService;

    Logger logger = LoggerFactory.getLogger(ScanService.class);

    public Health health() {
        try (Socket socket = new Socket(scanService.getClamavHost(), scanService.getClamavPort())) {
        } catch (IOException e) {
            logger.error("Failed to open socket to {}:{}", scanService.getClamavHost(), scanService.getClamavPort());
            return Health.down().withDetail("ClamAv Connection down", e.getMessage()).build();
        }

        return Health.up().build();
    }
}
