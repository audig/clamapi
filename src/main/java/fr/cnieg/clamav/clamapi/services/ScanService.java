package fr.cnieg.clamav.clamapi.services;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import fi.solita.clamav.ClamAVClient;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.cnieg.clamav.clamapi.beans.ClamAvResponse;

@Service
public class ScanService {

    private final Logger logger = LoggerFactory.getLogger(ScanService.class);

    @Value("${services.clamav.host}")
    @Getter
    private String clamavHost;

    @Value("${services.clamav.port}")
    @Getter
    private Integer clamavPort;

    @Value("${services.clamav.timeout}")
    private Integer clamavTimeout;

    private static ClamAVClient clamAVClient;

    @PostConstruct
    public void initConnection() {
        clamAVClient = new ClamAVClient(clamavHost, clamavPort, clamavTimeout);
        logger.info("Use clamav instance at {}:{} ", clamavHost, clamavPort);
    }

    public ClamAvResponse scan(final InputStream file) throws ClamAvException {
        try {
            byte[] reply = clamAVClient.scan(file);
            return new ClamAvResponse(!ClamAVClient.isCleanReply(reply), new String(reply, StandardCharsets.US_ASCII));
        } catch (final IOException e) {
            throw new ClamAvException(e.getMessage());
        } finally {
            try {
                file.close();
            } catch (IOException e) {
                logger.error("Failed to close InpuStream: {}", e.getMessage());
            }
        }
    }
}
