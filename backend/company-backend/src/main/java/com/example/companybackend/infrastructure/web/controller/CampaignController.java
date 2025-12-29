package com.example.companybackend.infrastructure.web.controller;

import com.example.companybackend.domain.model.Campaign;
import com.example.companybackend.domain.port.in.ProcessCampaignFileUseCase;
import com.example.companybackend.dto.CampaignResponseDto;
import com.example.companybackend.dto.UploadResponseDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/campaigns")
@CrossOrigin(origins = "*")
public class CampaignController {

    private final ProcessCampaignFileUseCase processCampaignFileUseCase;

    public CampaignController(ProcessCampaignFileUseCase processCampaignFileUseCase) {
        this.processCampaignFileUseCase = processCampaignFileUseCase;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponseDto> uploadCampaigns(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        
        ProcessCampaignFileUseCase.UploadResult result = processCampaignFileUseCase.execute(file.getInputStream());
        
        List<CampaignResponseDto> responseDtos = result.campaigns().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
        
        UploadResponseDto response = new UploadResponseDto(responseDtos, result.totalBudget());
        
        return ResponseEntity.ok(response);
    }

    private CampaignResponseDto mapToResponseDto(Campaign domain) {
        return new CampaignResponseDto(
                domain.getCampaignCode(),
                domain.getCampaignAcronym(),
                domain.getCompanyRuc(),
                domain.getCompanyName(),
                domain.getCampaignDescription(),
                domain.getCampaignDate(),
                domain.getNumberOfClients(),
                domain.getCampaignBudget()
        );
    }
}
