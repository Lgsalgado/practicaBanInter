export interface CampaignResponseDto {
  campaignCode: number;
  campaignAcronym: string;
  companyRuc: string;
  companyName: string;
  campaignDescription: string;
  campaignDate: string;
  numberOfClients: number;
  campaignBudget: number;
}

export interface UploadResponseDto {
  campaigns: CampaignResponseDto[];
  totalBudget: number;
}
