package com.example.companybackend.infrastructure.persistence.repository;

import com.example.companybackend.infrastructure.persistence.entity.CampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaCampaignRepository extends JpaRepository<CampaignEntity, Long> {
    List<CampaignEntity> findAllByCampaignCodeIn(List<Long> campaignCodes);
}
