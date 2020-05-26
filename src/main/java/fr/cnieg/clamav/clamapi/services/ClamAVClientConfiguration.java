package fr.cnieg.clamav.clamapi.services;

import fi.solita.clamav.ClamAVClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class ClamAVClientConfiguration {
    private final Logger logger = LoggerFactory.getLogger(ClamAVClientConfiguration.class);

    @Value("${services.clamav.host}")
    private String clamavHost;

    @Value("${services.clamav.port}")
    private Integer clamavPort;

    @Value("${services.clamav.timeout}")
    private Integer clamavTimeout;

    @Bean
    public ClamAVClient clamAVClient() {
        ClamAVClient clamAVClient = new ClamAVClient(clamavHost, clamavPort, clamavTimeout);
        logger.info("Use clamav instance at {}:{} ", clamavHost, clamavPort);
        return clamAVClient;
    }
}
