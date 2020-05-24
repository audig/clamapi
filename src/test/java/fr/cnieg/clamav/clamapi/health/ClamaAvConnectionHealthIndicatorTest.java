package fr.cnieg.clamav.clamapi.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.endpoint.http.ApiVersion;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.health.CompositeHealth;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpointWebExtension;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.cnieg.clamav.clamapi.services.ClamAVService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import({ ClamAVService.class })
public class ClamaAvConnectionHealthIndicatorTest {

    @Autowired
    ClamaAvConnectionHealthIndicator clamaAvConnectionHealthIndicator;

    @MockBean
    ClamAVService clamAVService;

    @Autowired
    HealthEndpointWebExtension healthEndpointWebExtension;

    private final String CLAMAV_CONNECTION_PROBE = "clamaAvConnection";

    @Test
    public void givenStatusOk_whenPing_thenReadinessProbeOk() throws Exception {

        doNothing().when(clamAVService).ping();
        clamaAvConnectionHealthIndicator.health();
        WebEndpointResponse<HealthComponent> response = healthEndpointWebExtension.health(ApiVersion.V3, SecurityContext.NONE, true, "readiness");

        CompositeHealth compositeHealth = (CompositeHealth) response.getBody();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Status.UP, compositeHealth.getComponents().get(CLAMAV_CONNECTION_PROBE).getStatus());
    }

    @Test
    public void givenStatusKo_whenPing_thenReadinessProbeKo() throws Exception {

        doThrow(new IOException("Can not ping clamav")).when(clamAVService).ping();
        clamaAvConnectionHealthIndicator.health();

        WebEndpointResponse<HealthComponent> response = healthEndpointWebExtension.health(ApiVersion.V3, SecurityContext.NONE, true, "readiness");

        CompositeHealth compositeHealth = (CompositeHealth) response.getBody();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), response.getStatus());
        assertEquals(Status.DOWN, compositeHealth.getComponents().get(CLAMAV_CONNECTION_PROBE).getStatus());
    }
}
