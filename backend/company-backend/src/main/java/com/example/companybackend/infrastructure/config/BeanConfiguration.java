package com.example.companybackend.infrastructure.config;

import com.example.companybackend.application.service.CampaignService;
import com.example.companybackend.domain.port.in.ProcessCampaignFileUseCase;
import com.example.companybackend.domain.port.out.CampaignRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ProcessCampaignFileUseCase processCampaignFileUseCase(CampaignRepositoryPort campaignRepositoryPort) {
        return new CampaignService(campaignRepositoryPort);
    }
}
