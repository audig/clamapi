package fr.cnieg.clamav.clamapi.services;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

import fi.solita.clamav.ClamAVClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ClamAVService {

    private final Logger logger = LoggerFactory.getLogger(ClamAVService.class);

    ClamAVClient clamAVClient;

    @Value("${services.clamav.host}")
    private String clamavHost;

    @Value("${services.clamav.port}")
    private Integer clamavPort;

    @Value("${services.clamav.timeout}")
    private Integer clamavTimeout;

    @PostConstruct
    public void initConnection() {
        clamAVClient = new ClamAVClient(clamavHost, clamavPort, clamavTimeout);
        logger.info("Use clamav instance at {}:{} ", clamavHost, clamavPort);
    }

    public byte[] scan(InputStream inputStream) throws ClamAvException {
        try {
            return clamAVClient.scan(inputStream);
        } catch (IOException e) {
            throw new ClamAvException(e.getMessage());
        }
    }

    public void ping() throws IOException {
        clamAVClient.ping();
    }
}
