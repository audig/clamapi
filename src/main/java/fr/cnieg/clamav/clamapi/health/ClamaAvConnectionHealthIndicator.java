package fr.cnieg.clamav.clamapi.health;

import java.io.IOException;

import fi.solita.clamav.ClamAVClient;
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
    private ClamAVClient clamAVClient;

    Logger logger = LoggerFactory.getLogger(ScanService.class);

    public Health health() {
        Health health = null;
        try {
            if (clamAVClient.ping()) {
                health = Health.up().build();
            } else {
                health = Health.down().build();
            }
        } catch (IOException ioException) {
            health = Health.down().build();
        }
        return health;
    }
}
