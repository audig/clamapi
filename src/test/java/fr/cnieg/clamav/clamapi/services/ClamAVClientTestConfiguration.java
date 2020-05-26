package fr.cnieg.clamav.clamapi.services;

import fi.solita.clamav.ClamAVClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Configuration
public class ClamAVClientTestConfiguration {

    @Bean
    @Primary
    public ClamAVClient clamAVClient() {
        return Mockito.mock(ClamAVClient.class);
    }
}
