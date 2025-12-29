package com.example.companybackend.infrastructure.persistence.adapter;

import com.example.companybackend.domain.model.Campaign;
import com.example.companybackend.domain.port.out.CampaignRepositoryPort;
import com.example.companybackend.infrastructure.persistence.entity.CampaignEntity;
import com.example.companybackend.infrastructure.persistence.mapper.CampaignMapper;
import com.example.companybackend.infrastructure.persistence.repository.JpaCampaignRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CampaignPersistenceAdapter implements CampaignRepositoryPort {

    private final JpaCampaignRepository jpaCampaignRepository;
    private final CampaignMapper campaignMapper;

    public CampaignPersistenceAdapter(JpaCampaignRepository jpaCampaignRepository, CampaignMapper campaignMapper) {
        this.jpaCampaignRepository = jpaCampaignRepository;
        this.campaignMapper = campaignMapper;
    }

    @Override
    public List<Campaign> findByCampaignCodes(List<Long> codes) {
        return jpaCampaignRepository.findAllByCampaignCodeIn(codes).stream()
                .map(campaignMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Campaign> saveAll(List<Campaign> campaigns) {
        List<CampaignEntity> entities = campaigns.stream()
                .map(campaignMapper::toEntity)
                .collect(Collectors.toList());
        
        return jpaCampaignRepository.saveAll(entities).stream()
                .map(campaignMapper::toDomain)
                .collect(Collectors.toList());
    }
}
