package com.example.companybackend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "campaign_code", nullable = false)
    private Long campaignCode;

    @Column(name = "campaign_acronym", length = 5, nullable = false)
    private String campaignAcronym;

    @Column(name = "company_ruc", nullable = false)
    private String companyRuc;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "campaign_description")
    private String campaignDescription;

    @Column(name = "campaign_date", nullable = false)
    private LocalDate campaignDate;

    @Column(name = "number_of_clients", nullable = false)
    private Integer numberOfClients;

    @Column(name = "campaign_budget", nullable = false)
    private BigDecimal campaignBudget;
}
