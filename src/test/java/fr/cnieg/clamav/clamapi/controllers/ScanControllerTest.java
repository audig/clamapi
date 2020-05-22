package fr.cnieg.clamav.clamapi.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import fr.cnieg.clamav.clamapi.beans.ClamAvResponse;
import fr.cnieg.clamav.clamapi.services.ScanService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ScanController.class)
@Import({ MetricsAutoConfiguration.class, CompositeMeterRegistryAutoConfiguration.class })
public class ScanControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ScanService scanService;

    private MockMultipartFile fileToScan;

    @BeforeEach
    public void setUp() throws IOException {
        Resource testFileResource = new ClassPathResource("test-files/test.pdf");
        fileToScan = new MockMultipartFile("file", testFileResource.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE,
            testFileResource.getInputStream());
        assertNotNull(fileToScan);
    }

    @Test
    public void givenClient_whenPostFileOkWithIdClient_thenReturnHttpStatusAccepted() throws Exception {

        Mockito.when(scanService.scan(Mockito.isA(InputStream.class))).thenReturn(new ClamAvResponse(false, ""));

        String idClient = "123456";

        mvc.perform(MockMvcRequestBuilders.multipart("/Scan").file(fileToScan).queryParam("idClient", idClient)
            .contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isAccepted());
    }

    @Test
    public void givenClient_whenPostFileOkWithoutIdClient_thenReturnHttpStatusAccepted() throws Exception {

        Mockito.when(scanService.scan(Mockito.isA(InputStream.class))).thenReturn(new ClamAvResponse(false, ""));

        mvc.perform(MockMvcRequestBuilders.multipart("/Scan").file(fileToScan).contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(
            status().isAccepted());
    }

    @Test
    public void givenClient_whenPostFileKoWithIdClient_thenReturnHttpStatusAccepted() throws Exception {

        Mockito.when(scanService.scan(Mockito.isA(InputStream.class))).thenReturn(new ClamAvResponse(true, "stream: Win.Test.EICAR_HDB-1 FOUND"));

        String idClient = "123456";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.multipart("/Scan").file(fileToScan).queryParam("idClient", idClient)
            .contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isNotAcceptable()).andReturn();

        String expectedResponseBody =
            "File " + fileToScan.getOriginalFilename() + " infected for IdClient " + idClient + ": stream: Win.Test.EICAR_HDB-1 FOUND";
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertEquals(expectedResponseBody, actualResponseBody);
    }
}
