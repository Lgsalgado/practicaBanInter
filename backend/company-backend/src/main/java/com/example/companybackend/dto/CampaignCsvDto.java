package com.example.companybackend.dto;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CampaignCsvDto {

    @CsvBindByPosition(position = 0)
    private Long campaignCode;

    @CsvBindByPosition(position = 1)
    private String campaignAcronym;

    @CsvBindByPosition(position = 2)
    private String companyRuc;

    @CsvBindByPosition(position = 3)
    private String companyName;

    @CsvBindByPosition(position = 4)
    private String campaignDescription;

    @CsvBindByPosition(position = 5)
    @CsvDate("yyyy-MM-dd")
    private LocalDate campaignDate;

    @CsvBindByPosition(position = 6)
    private Integer numberOfClients;

    @CsvBindByPosition(position = 7)
    private BigDecimal campaignBudget;
}
