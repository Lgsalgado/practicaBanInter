package com.example.companybackend.domain.port.in;

import com.example.companybackend.domain.model.Campaign;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

public interface ProcessCampaignFileUseCase {
    UploadResult execute(InputStream fileInputStream);

    record UploadResult(List<Campaign> campaigns, BigDecimal totalBudget) {}
}
