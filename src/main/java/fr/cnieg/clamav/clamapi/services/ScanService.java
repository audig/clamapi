package fr.cnieg.clamav.clamapi.services;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import fi.solita.clamav.ClamAVClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.cnieg.clamav.clamapi.beans.ClamAvResponse;

@Service
public class ScanService {

    private final Logger logger = LoggerFactory.getLogger(ScanService.class);

    @Autowired
    private ClamAVService clamAVService;

    public ClamAvResponse scan(final InputStream file) throws ClamAvException {
        byte[] reply = clamAVService.scan(file);
        return new ClamAvResponse(!ClamAVClient.isCleanReply(reply), new String(reply, StandardCharsets.US_ASCII));
    }
}
