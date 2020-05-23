package fr.cnieg.clamav.clamapi.controllers;

import java.io.IOException;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.cnieg.clamav.clamapi.beans.ClamAvResponse;
import fr.cnieg.clamav.clamapi.services.ClamAvException;
import fr.cnieg.clamav.clamapi.services.ScanService;

@Tag(name = "ScanService", description = "Api to submit a file and get the result of the scan. idClient is used to log if the file is infected")
@RestController
public class ScanController {

    Logger logger = LoggerFactory.getLogger(ScanService.class);

    @Autowired
    ScanService scanService;

    Counter scanCounter;
    Counter infectedCounter;
    Timer scanTimer;

    public ScanController(MeterRegistry registry) {
        scanCounter = registry.counter("clamav_scan");

        // register a counter of questionable usefulness
        infectedCounter = registry.counter("clamav_infected");

        // register a timer -- though for request timing it is easier to use @Timed
        scanTimer = registry.timer("scan_duration");
    }

    @PostMapping("/Scan")
    public ResponseEntity scan(@RequestParam(value = "idClient", defaultValue = "UNDEFINED") String idClient,
        @RequestPart(value = "file") MultipartFile file) throws IOException, ClamAvException {
        return scanTimer.record(() -> {
            ClamAvResponse clamAvResponse = null;
            try {
                clamAvResponse = scanService.scan(file.getInputStream());
            } catch (Exception e) {
                logger.error("Error when get inputstream from file {}", file.getOriginalFilename());
            }
            scanCounter.increment();
            if (clamAvResponse.isInfected()) {
                String message = String.format(
                    "File " + file.getOriginalFilename() + " infected for IdClient " + idClient + ": " + clamAvResponse.getMessage());
                logger.info(message);
                infectedCounter.increment();
                return new ResponseEntity(message, HttpStatus.NOT_ACCEPTABLE);
            }

            return new ResponseEntity(HttpStatus.ACCEPTED);
        });
    }
}
