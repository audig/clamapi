package fr.cnieg.clamav.clamapi.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import fi.solita.clamav.ClamAVClient;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.cnieg.clamav.clamapi.beans.ClamAvResponse;

@Service
public class ScanService {

    private final Logger logger = LoggerFactory.getLogger(ScanService.class);

    @Autowired
    ClamAVClient clamAVClient;

    @Autowired
    MeterRegistry meterRegistry;

    Counter scanCounter;
    Counter scanInfectedCounter;

    public ScanService(MeterRegistry registry) {
        scanCounter = registry.counter("clamav_scan");
        scanInfectedCounter = registry.counter("clamav_scan_infected");
    }

    @Timed("clamav_scan")
    public ClamAvResponse scan(final InputStream file) throws IOException {
        byte[] reply = clamAVClient.scan(file);
        ClamAvResponse clamAvResponse = new ClamAvResponse(!ClamAVClient.isCleanReply(reply), new String(reply, StandardCharsets.US_ASCII));
        scanCounter.increment();
        if (clamAvResponse.isInfected()) {
            scanInfectedCounter.increment();
        }
        return clamAvResponse;
    }
}
