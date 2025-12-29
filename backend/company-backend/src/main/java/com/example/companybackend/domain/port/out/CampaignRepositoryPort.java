package com.example.companybackend.domain.port.out;

import com.example.companybackend.domain.model.Campaign;
import java.util.List;

public interface CampaignRepositoryPort {
    List<Campaign> findByCampaignCodes(List<Long> codes);
    List<Campaign> saveAll(List<Campaign> campaigns);
}
