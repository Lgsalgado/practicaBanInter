package com.example.companybackend.application.service;

import com.example.companybackend.domain.model.Campaign;
import com.example.companybackend.domain.port.in.ProcessCampaignFileUseCase;
import com.example.companybackend.domain.port.out.CampaignRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepositoryPort campaignRepositoryPort;

    private CampaignService campaignService;

    @BeforeEach
    void setUp() {
        campaignService = new CampaignService(campaignRepositoryPort);
    }

    @Test
    void shouldProcessValidCsvAndReturnSortedCampaigns() {
        //GIVEN
        String csvContent = "1,MARKT,0992724919001,TONY CORP,DESC,2025-12-26,10,100000\n" +
                            "2,TEST,1790016919001,FAVORITA,DESC2,2025-12-26,12,250000";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        Campaign c1 = new Campaign(null, 1L, "MARKT", "0992724919001", "TONY CORP", "DESC", LocalDate.of(2025, 12, 26), 10, new BigDecimal("100000"));
        Campaign c2 = new Campaign(null, 2L, "TEST", "1790016919001", "FAVORITA", "DESC2", LocalDate.of(2025, 12, 26), 12, new BigDecimal("250000"));

        when(campaignRepositoryPort.findByCampaignCodes(anyList())).thenReturn(Collections.emptyList());
        when(campaignRepositoryPort.saveAll(anyList())).thenReturn(List.of(c1, c2));

        //WHEN
        ProcessCampaignFileUseCase.UploadResult result = campaignService.execute(inputStream);

        //THEN
        assertNotNull(result);
        assertEquals(2, result.campaigns().size());
        assertEquals(new BigDecimal("350000"), result.totalBudget());
        assertEquals(1L, result.campaigns().get(0).getCampaignCode()); // Ordenado por presupuesto (100k < 250k)
    }

    @Test
    void shouldThrowExceptionWhenAcronymIsInvalid() {
        //GIVEN
        String csvContent = "1,12345,0992724919001,TONY CORP,DESC,2025-12-26,10,100000"; // Acrónimo numérico inválido
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        //WHEN & THEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            campaignService.execute(inputStream);
        });
        assertTrue(exception.getMessage().contains("acrónimo"));
    }

    @Test
    void shouldThrowExceptionWhenDateIsInvalid() {
        //GIVEN
        String csvContent = "1,MARKT,0992724919001,TONY CORP,DESC,26-12-2025,10,100000"; // Formato fecha incorrecto
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        //WHEN & THEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            campaignService.execute(inputStream);
        });
        assertTrue(exception.getMessage().contains("formato"));
    }
}
