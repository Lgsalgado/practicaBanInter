package com.example.companybackend.infrastructure.persistence.mapper;

import com.example.companybackend.domain.model.Campaign;
import com.example.companybackend.infrastructure.persistence.entity.CampaignEntity;
import org.springframework.stereotype.Component;

@Component
public class CampaignMapper {

    public Campaign toDomain(CampaignEntity entity) {
        if (entity == null) return null;
        return new Campaign(
                entity.getId(),
                entity.getCampaignCode(),
                entity.getCampaignAcronym(),
                entity.getCompanyRuc(),
                entity.getCompanyName(),
                entity.getCampaignDescription(),
                entity.getCampaignDate(),
                entity.getNumberOfClients(),
                entity.getCampaignBudget()
        );
    }

    public CampaignEntity toEntity(Campaign domain) {
        if (domain == null) return null;
        return new CampaignEntity(
                domain.getId(),
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
