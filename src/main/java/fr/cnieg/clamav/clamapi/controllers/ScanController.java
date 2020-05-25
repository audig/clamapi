package fr.cnieg.clamav.clamapi.controllers;

import java.io.IOException;

import io.micrometer.core.annotation.Timed;
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
@Timed(value = "scan_api")
public class ScanController {

    Logger logger = LoggerFactory.getLogger(ScanService.class);

    @Autowired
    ScanService scanService;

    @PostMapping("/Scan")
    public ResponseEntity scan(@RequestParam(value = "idClient", defaultValue = "UNDEFINED") String idClient,
        @RequestPart(value = "file") MultipartFile file) throws IOException, ClamAvException {

        ClamAvResponse clamAvResponse = null;
        try {
            clamAvResponse = scanService.scan(file.getInputStream());
            if (clamAvResponse.isInfected()) {
                String message = "File " + file.getOriginalFilename() + " infected for IdClient " + idClient + ": " + clamAvResponse.getMessage();
                logger.info(message);
                return new ResponseEntity(message, HttpStatus.NOT_ACCEPTABLE);
            }
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error when get inputstream from file {}", file.getOriginalFilename());
            throw e;
        }
    }
}