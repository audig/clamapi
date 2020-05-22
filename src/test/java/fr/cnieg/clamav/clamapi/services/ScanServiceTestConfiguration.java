package fr.cnieg.clamav.clamapi.services;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Configuration
public class ScanServiceTestConfiguration {

    @Bean
    @Primary
    public ScanService scanService() {
        return Mockito.mock(ScanService.class);
    }
}
