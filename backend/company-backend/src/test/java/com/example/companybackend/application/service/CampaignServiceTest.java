package com.example.companybackend.application.service;

import com.example.companybackend.domain.model.Campaign;
import com.example.companybackend.domain.port.in.ProcessCampaignFileUseCase;
import com.example.companybackend.domain.port.out.CampaignRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepositoryPort campaignRepositoryPort;

    @Captor
    private ArgumentCaptor<List<Campaign>> campaignsCaptor;

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
        assertEquals(1L, result.campaigns().get(0).getCampaignCode());
        
        verify(campaignRepositoryPort).saveAll(campaignsCaptor.capture());
        List<Campaign> capturedCampaigns = campaignsCaptor.getValue();
        assertEquals(2, capturedCampaigns.size());
        
        Campaign capturedC1 = capturedCampaigns.stream().filter(c -> c.getCampaignCode() == 1L).findFirst().orElseThrow();
        assertEquals("DESC", capturedC1.getCampaignDescription());
        assertEquals("TONY CORP", capturedC1.getCompanyName());
    }

    @Test
    void shouldUpdateExistingCampaign() {
        //GIVEN
        String csvContent = "1,UPDTD,0992724919001,TONY CORP,DESC_UPDATED,2025-12-26,20,200000";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        Campaign existingCampaign = new Campaign(100L, 1L, "OLD", "OLD_RUC", "OLD_NAME", "OLD_DESC", LocalDate.now(), 5, BigDecimal.ONE);

        when(campaignRepositoryPort.findByCampaignCodes(anyList())).thenReturn(List.of(existingCampaign));
        when(campaignRepositoryPort.saveAll(anyList())).thenReturn(List.of(existingCampaign));

        //WHEN
        campaignService.execute(inputStream);

        //THEN
        verify(campaignRepositoryPort).saveAll(campaignsCaptor.capture());
        List<Campaign> capturedCampaigns = campaignsCaptor.getValue();
        assertEquals(1, capturedCampaigns.size());
        
        Campaign updatedCampaign = capturedCampaigns.get(0);
        assertEquals(100L, updatedCampaign.getId());
        assertEquals("UPDTD", updatedCampaign.getCampaignAcronym());
        assertEquals("DESC_UPDATED", updatedCampaign.getCampaignDescription());
        assertEquals(new BigDecimal("200000"), updatedCampaign.getCampaignBudget());
    }

    @Test
    void shouldThrowExceptionWhenAcronymIsInvalid() {
        String csvContent = "1,12345,0992724919001,TONY CORP,DESC,2025-12-26,10,100000";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        assertThrows(IllegalArgumentException.class, () -> campaignService.execute(inputStream));
    }

    @Test
    void shouldThrowExceptionWhenAcronymIsTooLong() {
        String csvContent = "1,ABCDEF,0992724919001,TONY CORP,DESC,2025-12-26,10,100000";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        assertThrows(IllegalArgumentException.class, () -> campaignService.execute(inputStream));
    }
    
    @Test
    void shouldThrowExceptionWhenAcronymIsMissing() {
        String csvContent = "1,,0992724919001,TONY CORP,DESC,2025-12-26,10,100000";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> campaignService.execute(inputStream));
        assertTrue(ex.getMessage().contains("acrónimo"));
    }

    @Test
    void shouldThrowExceptionWhenDateIsInvalid() {
        String csvContent = "1,MARKT,0992724919001,TONY CORP,DESC,26-12-2025,10,100000";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        assertThrows(IllegalArgumentException.class, () -> campaignService.execute(inputStream));
    }
    
    @Test
    void shouldThrowExceptionWhenDateIsMissing() {
        String csvContent = "1,MARKT,0992724919001,TONY CORP,DESC,,10,100000";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> campaignService.execute(inputStream));
        assertTrue(ex.getMessage().contains("fecha de la campaña"));
    }

    @Test
    void shouldThrowExceptionWhenBudgetIsNotNumeric() {
        String csvContent = "1,MARKT,0992724919001,TONY CORP,DESC,2025-12-26,10,CIENMIL";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        assertThrows(IllegalArgumentException.class, () -> campaignService.execute(inputStream));
    }

    @Test
    void shouldThrowExceptionWhenRequiredFieldIsMissing() {
        String csvContent = "1,MARKT,,TONY CORP,DESC,2025-12-26,10,100000"; // RUC vacío
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> campaignService.execute(inputStream));
        assertTrue(ex.getMessage().contains("RUC"));
    }
    
    @Test
    void shouldThrowExceptionWhenCompanyNameIsMissing() {
        String csvContent = "1,MARKT,0992724919001,,DESC,2025-12-26,10,100000"; // Nombre vacío
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> campaignService.execute(inputStream));
        assertTrue(ex.getMessage().contains("nombre de la empresa"));
    }
    
    @Test
    void shouldThrowExceptionWhenDescriptionIsMissing() {
        String csvContent = "1,MARKT,0992724919001,TONY CORP,,2025-12-26,10,100000"; // Descripción vacía
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> campaignService.execute(inputStream));
        assertTrue(ex.getMessage().contains("descripción"));
    }
    
    @Test
    void shouldThrowExceptionWhenClientsIsMissing() {
        String csvContent = "1,MARKT,0992724919001,TONY CORP,DESC,2025-12-26,,100000"; // Clientes vacío
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> campaignService.execute(inputStream));
        assertTrue(ex.getMessage().contains("número de clientes"));
    }
    
    @Test
    void shouldThrowExceptionWhenBudgetIsMissing() {
        String csvContent = "1,MARKT,0992724919001,TONY CORP,DESC,2025-12-26,10,"; // Presupuesto vacío
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> campaignService.execute(inputStream));
        assertTrue(ex.getMessage().contains("presupuesto"));
    }
    
    @Test
    void shouldThrowExceptionWhenCampaignCodeIsMissing() {
        String csvContent = ",MARKT,0992724919001,TONY CORP,DESC,2025-12-26,10,100000"; // Código vacío
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> campaignService.execute(inputStream));
        assertTrue(ex.getMessage().contains("código de campaña"));
    }
    
    @Test
    void shouldWrapUnexpectedExceptions() {
        //GIVEN
        String csvContent = "1,MARKT,0992724919001,TONY CORP,DESC,2025-12-26,10,100000";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        when(campaignRepositoryPort.findByCampaignCodes(anyList())).thenThrow(new RuntimeException("DB Connection Failed"));

        //WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class, () -> campaignService.execute(inputStream));
        assertTrue(ex.getMessage().contains("Error processing CSV file"));
        assertTrue(ex.getCause().getMessage().contains("DB Connection Failed"));
    }
}
