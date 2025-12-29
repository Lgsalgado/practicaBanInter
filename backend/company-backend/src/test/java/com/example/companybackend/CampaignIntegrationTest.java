package com.example.companybackend;

import com.example.companybackend.infrastructure.persistence.entity.CampaignEntity;
import com.example.companybackend.infrastructure.persistence.repository.JpaCampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CampaignIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaCampaignRepository jpaCampaignRepository;

    @BeforeEach
    void setUp() {
        jpaCampaignRepository.deleteAll();
    }

    @Test
    void shouldProcessCsvAndPersistDataInDatabase() throws Exception {
        //GIVEN
        String csvContent = "1,MARKT,0992724919001,TONY CORP,DESC,2025-12-26,10,100000\n" +
                            "2,TEST,1790016919001,FAVORITA,DESC2,2025-12-26,12,250000";
        
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "campaigns.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes()
        );

        //WHEN
        mockMvc.perform(multipart("/api/campaigns/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBudget").value(350000))
                .andExpect(jsonPath("$.campaigns[0].campaignCode").value(1))
                .andExpect(jsonPath("$.campaigns[1].campaignCode").value(2));

        //THEN (Verificar persistencia real en H2)
        List<CampaignEntity> savedCampaigns = jpaCampaignRepository.findAll();
        assertEquals(2, savedCampaigns.size());
        
        CampaignEntity c1 = savedCampaigns.stream().filter(c -> c.getCampaignCode() == 1L).findFirst().orElseThrow();
        assertEquals("MARKT", c1.getCampaignAcronym());
        assertEquals(new BigDecimal("100000.00"), c1.getCampaignBudget());
    }

    @Test
    void shouldUpdateExistingCampaignInsteadOfDuplicating() throws Exception {
        //GIVEN (Primera carga)
        String csvContent1 = "1,MARKT,0992724919001,TONY CORP,DESC,2025-12-26,10,100000";
        MockMultipartFile file1 = new MockMultipartFile("file", "file1.csv", MediaType.TEXT_PLAIN_VALUE, csvContent1.getBytes());
        
        mockMvc.perform(multipart("/api/campaigns/upload").file(file1)).andExpect(status().isOk());

        //WHEN (Segunda carga con datos actualizados para el mismo código)
        String csvContent2 = "1,UPDTD,0992724919001,TONY CORP,DESC,2025-12-26,20,200000";
        MockMultipartFile file2 = new MockMultipartFile("file", "file2.csv", MediaType.TEXT_PLAIN_VALUE, csvContent2.getBytes());
        
        mockMvc.perform(multipart("/api/campaigns/upload").file(file2)).andExpect(status().isOk());

        //THEN
        List<CampaignEntity> savedCampaigns = jpaCampaignRepository.findAll();
        assertEquals(1, savedCampaigns.size());
        
        CampaignEntity updatedCampaign = savedCampaigns.get(0);
        assertEquals("UPDTD", updatedCampaign.getCampaignAcronym());
        assertEquals(new BigDecimal("200000.00"), updatedCampaign.getCampaignBudget());
    }
}
