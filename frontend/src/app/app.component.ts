import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CampaignUploadComponent } from './components/campaign-upload/campaign-upload.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, CampaignUploadComponent],
  template: '<app-campaign-upload></app-campaign-upload>',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'frontend';
}
