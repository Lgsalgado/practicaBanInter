import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CampaignService } from '../../services/campaign.service';
import { UploadResponseDto } from '../../models/campaign.model';

@Component({
  selector: 'app-campaign-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './campaign-upload.component.html',
  styleUrls: ['./campaign-upload.component.css']
})
export class CampaignUploadComponent {
  selectedFile: File | null = null;
  uploadResponse: UploadResponseDto | null = null;
  errorMessage: string | null = null;
  isLoading: boolean = false;

  constructor(private campaignService: CampaignService) {}

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      if (!file.name.toLowerCase().endsWith('.csv')) {
        this.errorMessage = 'El archivo seleccionado no es un CSV válido. Por favor seleccione un archivo con extensión .csv';
        this.selectedFile = null;
        event.target.value = ''; 
        return;
      }

      this.selectedFile = file;
      this.errorMessage = null;
    }
  }

  onUpload(): void {
    if (!this.selectedFile) {
      this.errorMessage = 'Por favor seleccione un archivo CSV.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.uploadResponse = null;

    this.campaignService.uploadFile(this.selectedFile).subscribe({
      next: (response) => {
        this.uploadResponse = response;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error;
        this.isLoading = false;
      }
    });
  }
}
