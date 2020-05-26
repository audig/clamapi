package fr.cnieg.clamav.clamapi.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import fi.solita.clamav.ClamAVClient;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.cnieg.clamav.clamapi.beans.ClamAvResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import({ ScanService.class })
public class ScanServiceTest {

    private InputStream fileToScan;

    @Autowired
    ScanService scanService;

    @MockBean
    ClamAVClient clamAVClient;

    @Autowired
    MeterRegistry meterRegistry;

    @BeforeEach
    public void setUp() throws IOException {
        Resource testFileResource = new ClassPathResource("test-files/test.pdf");
        fileToScan = testFileResource.getInputStream();

        assertNotNull(fileToScan);
    }

    @Test
    public void givenFile_whenScan_thenReturnNotInfected() throws Exception {

        byte[] responseClamav = "OK".getBytes();
        double lastValueOfClamAvScanCounter = meterRegistry.get("clamav_scan").counter().count();
        double lastValueOfClamAvScanInfectedCounter = meterRegistry.get("clamav_scan_infected").counter().count();

        Mockito.when(clamAVClient.scan(Mockito.isA(InputStream.class))).thenReturn(responseClamav);
        ClamAvResponse clamAvResponse = scanService.scan(fileToScan);

        assertFalse(clamAvResponse.isInfected());
        assertEquals(++lastValueOfClamAvScanCounter, meterRegistry.get("clamav_scan").counter().count());
        assertEquals(lastValueOfClamAvScanInfectedCounter, meterRegistry.get("clamav_scan_infected").counter().count());
        assertTrue(meterRegistry.get("clamav_scan").timer().mean(TimeUnit.NANOSECONDS) > 0);
    }

    @Test
    public void givenFile_whenScan_thenReturnInfected() throws Exception {

        byte[] responseClamav = "FOUND".getBytes();
        double lastValueOfClamAvScanCounter = meterRegistry.get("clamav_scan").counter().count();
        double lastValueOfClamAvScanInfectedCounter = meterRegistry.get("clamav_scan_infected").counter().count();

        Mockito.when(clamAVClient.scan(Mockito.isA(InputStream.class))).thenReturn(responseClamav);

        ClamAvResponse clamAvResponse = scanService.scan(fileToScan);

        assertTrue(clamAvResponse.isInfected());
        assertEquals(++lastValueOfClamAvScanCounter, meterRegistry.get("clamav_scan").counter().count());
        assertEquals(++lastValueOfClamAvScanInfectedCounter, meterRegistry.get("clamav_scan_infected").counter().count());
        assertTrue(meterRegistry.get("clamav_scan").timer().mean(TimeUnit.NANOSECONDS) > 0);
    }

    @Test
    public void givenFile_whenScan_thenReturnIoException() throws Exception {

        String expectedExceptionMessage = "Cannot connect to ClamAv";
        double lastValueOfClamAvScanCounter = meterRegistry.get("clamav_scan").counter().count();
        double lastValueOfClamAvScanInfectedCounter = meterRegistry.get("clamav_scan_infected").counter().count();

        Mockito.when(clamAVClient.scan(Mockito.isA(InputStream.class))).thenThrow(new IOException(expectedExceptionMessage));

        IOException exception = assertThrows(IOException.class, () -> {
            scanService.scan(fileToScan);
        });

        assertEquals(expectedExceptionMessage, exception.getMessage());
        assertEquals(lastValueOfClamAvScanCounter, meterRegistry.get("clamav_scan").counter().count());
        assertEquals(lastValueOfClamAvScanInfectedCounter, meterRegistry.get("clamav_scan_infected").counter().count());
    }
}
