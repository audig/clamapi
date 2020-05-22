package fr.cnieg.clamav.clamapi.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.cnieg.clamav.clamapi.services.ClamAVService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import({ ClamAVService.class })
public class ClamaAvConnectionHealthIndicatorTest {

    @Autowired
    ApplicationAvailability availability;

    @Autowired
    ClamaAvConnectionHealthIndicator clamaAvConnectionHealthIndicator;

    @MockBean
    ClamAVService clamAVService;

    @Test
    public void givenStatusOk_whenPing_thenReadinessProbeOk() throws Exception {

        doNothing().when(clamAVService).ping();
        Health health = clamaAvConnectionHealthIndicator.health();
        assertEquals("UP", health.getStatus().toString());
    }

    @Test
    public void givenStatusKo_whenPing_thenReadinessProbeKo() throws Exception {

        doThrow(new IOException("Can not ping clamav")).when(clamAVService).ping();
        Health health = clamaAvConnectionHealthIndicator.health();
        assertEquals("DOWN", health.getStatus().toString());
    }
}
