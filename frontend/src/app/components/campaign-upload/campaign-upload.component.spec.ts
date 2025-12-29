import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CampaignUploadComponent } from './campaign-upload.component';
import { CampaignService } from '../../services/campaign.service';
import { of, throwError } from 'rxjs';
import { UploadResponseDto } from '../../models/campaign.model';

describe('CampaignUploadComponent', () => {
  let component: CampaignUploadComponent;
  let fixture: ComponentFixture<CampaignUploadComponent>;
  let campaignServiceSpy: jasmine.SpyObj<CampaignService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('CampaignService', ['uploadFile']);

    await TestBed.configureTestingModule({
      imports: [CampaignUploadComponent],
      providers: [
        { provide: CampaignService, useValue: spy }
      ]
    })
    .compileComponents();

    campaignServiceSpy = TestBed.inject(CampaignService) as jasmine.SpyObj<CampaignService>;
    fixture = TestBed.createComponent(CampaignUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate CSV file extension', () => {
    const mockFile = new File([''], 'test.txt', { type: 'text/plain' });
    const event = { target: { files: [mockFile], value: 'test.txt' } };

    component.onFileSelected(event);

    expect(component.errorMessage).toContain('no es un CSV válido');
    expect(component.selectedFile).toBeNull();
    expect(event.target.value).toBe('');
  });

  it('should accept valid CSV file', () => {
    const mockFile = new File([''], 'test.csv', { type: 'text/csv' });
    const event = { target: { files: [mockFile] } };

    component.onFileSelected(event);

    expect(component.errorMessage).toBeNull();
    expect(component.selectedFile).toBe(mockFile);
  });

  it('should call upload service on valid file', () => {
    const mockFile = new File([''], 'test.csv', { type: 'text/csv' });
    const mockResponse: UploadResponseDto = { campaigns: [], totalBudget: 0 };
    
    component.selectedFile = mockFile;
    campaignServiceSpy.uploadFile.and.returnValue(of(mockResponse));

    component.onUpload();

    expect(component.isLoading).toBeFalse();
    expect(component.uploadResponse).toEqual(mockResponse);
    expect(campaignServiceSpy.uploadFile).toHaveBeenCalledWith(mockFile);
  });

  it('should handle upload error', () => {
    const mockFile = new File([''], 'test.csv', { type: 'text/csv' });
    const errorMessage = 'Upload failed';
    
    component.selectedFile = mockFile;
    campaignServiceSpy.uploadFile.and.returnValue(throwError(() => errorMessage));

    component.onUpload();

    expect(component.isLoading).toBeFalse();
    expect(component.errorMessage).toBe(errorMessage);
    expect(component.uploadResponse).toBeNull();
  });
});
