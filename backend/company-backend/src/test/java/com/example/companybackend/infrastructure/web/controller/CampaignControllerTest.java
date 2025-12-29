package com.example.companybackend.infrastructure.web.controller;

import com.example.companybackend.domain.model.Campaign;
import com.example.companybackend.domain.port.in.ProcessCampaignFileUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CampaignController.class)
class CampaignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessCampaignFileUseCase processCampaignFileUseCase;

    @Test
    void shouldUploadFileAndReturn200() throws Exception {
        //GIVEN
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "campaigns.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "content".getBytes()
        );

        Campaign campaign = new Campaign(1L, 1L, "MARKT", "RUC", "NAME", "DESC", LocalDate.now(), 10, BigDecimal.TEN);
        ProcessCampaignFileUseCase.UploadResult result = new ProcessCampaignFileUseCase.UploadResult(
                List.of(campaign),
                BigDecimal.TEN
        );

        when(processCampaignFileUseCase.execute(any())).thenReturn(result);

        //WHEN & THEN
        mockMvc.perform(multipart("/api/campaigns/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBudget").value(10))
                .andExpect(jsonPath("$.campaigns[0].campaignCode").value(1));
    }

    @Test
    void shouldReturn400WhenFileIsEmpty() throws Exception {
        //GIVEN
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.csv",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );

        //WHEN & THEN
        mockMvc.perform(multipart("/api/campaigns/upload").file(emptyFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenValidationFails() throws Exception {
        //GIVEN
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "campaigns.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "content".getBytes()
        );

        when(processCampaignFileUseCase.execute(any())).thenThrow(new IllegalArgumentException("Error de validación"));

        //WHEN & THEN
        mockMvc.perform(multipart("/api/campaigns/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de Validación"));
    }

    @Test
    void shouldReturn500WhenIoErrorOccurs() throws Exception {
        //GIVEN
        InputStream errorInputStream = mock(InputStream.class);
        when(errorInputStream.read(any())).thenThrow(new IOException("Error de lectura"));
        when(processCampaignFileUseCase.execute(any())).thenThrow(new RuntimeException("Error inesperado"));

        //WHEN & THEN
        mockMvc.perform(multipart("/api/campaigns/upload").file(new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error Interno"));
    }
}
