package com.example.companybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponseDto {
    private List<CampaignResponseDto> campaigns;
    private BigDecimal totalBudget;
}
