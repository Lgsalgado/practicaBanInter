package com.example.companybackend.application.service;

import com.example.companybackend.domain.model.Campaign;
import com.example.companybackend.domain.port.in.ProcessCampaignFileUseCase;
import com.example.companybackend.domain.port.out.CampaignRepositoryPort;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CampaignService implements ProcessCampaignFileUseCase {

    private final CampaignRepositoryPort campaignRepositoryPort;
    private static final Pattern ACRONYM_PATTERN = Pattern.compile("^[a-zA-Z]+$");

    public CampaignService(CampaignRepositoryPort campaignRepositoryPort) {
        this.campaignRepositoryPort = campaignRepositoryPort;
    }

    @Override
    public UploadResult execute(InputStream fileInputStream) {
        try (Reader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
            CsvToBean<CampaignCsvDto> csvToBean = new CsvToBeanBuilder<CampaignCsvDto>(reader)
                    .withType(CampaignCsvDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(',')
                    .build();

            List<CampaignCsvDto> csvDtos;
            try {
                csvDtos = csvToBean.parse();
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("El archivo no cumple con el formato esperado: " + e.getMessage());
            }

            List<Long> campaignCodes = csvDtos.stream()
                    .map(CampaignCsvDto::getCampaignCode)
                    .collect(Collectors.toList());

            List<Campaign> existingCampaigns = campaignRepositoryPort.findByCampaignCodes(campaignCodes);
            Map<Long, Campaign> existingCampaignsMap = existingCampaigns.stream()
                    .collect(Collectors.toMap(Campaign::getCampaignCode, Function.identity()));

            List<Campaign> campaignsToSave = new ArrayList<>();

            for (CampaignCsvDto dto : csvDtos) {
                validateDto(dto);
                
                Campaign campaign = existingCampaignsMap.get(dto.getCampaignCode());
                if (campaign == null) {
                    campaign = new Campaign();
                    campaign.setCampaignCode(dto.getCampaignCode());
                }
                
                campaign.setCampaignAcronym(dto.getCampaignAcronym());
                campaign.setCompanyRuc(dto.getCompanyRuc());
                campaign.setCompanyName(dto.getCompanyName());
                campaign.setCampaignDescription(dto.getCampaignDescription());
                campaign.setCampaignDate(dto.getCampaignDate());
                campaign.setNumberOfClients(dto.getNumberOfClients());
                campaign.setCampaignBudget(dto.getCampaignBudget());
                
                campaignsToSave.add(campaign);
            }

            List<Campaign> savedCampaigns = campaignRepositoryPort.saveAll(campaignsToSave);

            List<Campaign> sortedCampaigns = savedCampaigns.stream()
                    .sorted(Comparator.comparing(Campaign::getCampaignBudget))
                    .collect(Collectors.toList());

            BigDecimal totalBudget = sortedCampaigns.stream()
                    .map(Campaign::getCampaignBudget)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new UploadResult(sortedCampaigns, totalBudget);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file: " + e.getMessage(), e);
        }
    }

    private void validateDto(CampaignCsvDto dto) {
        if (dto.getCampaignCode() == null) {
            throw new IllegalArgumentException("El código de campaña debe ser numérico.");
        }
        if (dto.getCampaignAcronym() == null || dto.getCampaignAcronym().length() > 5 || !ACRONYM_PATTERN.matcher(dto.getCampaignAcronym()).matches()) {
            throw new IllegalArgumentException("El acrónimo de campaña debe contener solo texto y tener un máximo de 5 caracteres.");
        }
        if (dto.getCompanyRuc() == null || dto.getCompanyRuc().isEmpty()) {
            throw new IllegalArgumentException("El RUC de la empresa es requerido.");
        }
        if (dto.getCompanyName() == null || dto.getCompanyName().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la empresa es requerido.");
        }
        if (dto.getCampaignDescription() == null || dto.getCampaignDescription().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la campaña es requerida.");
        }
        if (dto.getCampaignDate() == null) {
            throw new IllegalArgumentException("La fecha de la campaña debe cumplir el formato yyyy-MM-dd.");
        }
        if (dto.getNumberOfClients() == null) {
            throw new IllegalArgumentException("El número de clientes debe ser un valor numérico.");
        }
        if (dto.getCampaignBudget() == null) {
            throw new IllegalArgumentException("El presupuesto de la campaña debe ser un valor numérico.");
        }
    }

    @Data
    public static class CampaignCsvDto {
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
}
