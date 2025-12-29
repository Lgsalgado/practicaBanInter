package com.example.companybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignResponseDto {
    private Long campaignCode;
    private String campaignAcronym;
    private String companyRuc;
    private String companyName;
    private String campaignDescription;
    private LocalDate campaignDate;
    private Integer numberOfClients;
    private BigDecimal campaignBudget;
}
