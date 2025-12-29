import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CampaignService } from './campaign.service';
import { environment } from '../../environments/environment';
import { UploadResponseDto } from '../models/campaign.model';

describe('CampaignService', () => {
  let service: CampaignService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CampaignService]
    });
    service = TestBed.inject(CampaignService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should upload file and return response', () => {
    const mockFile = new File([''], 'test.csv', { type: 'text/csv' });
    const mockResponse: UploadResponseDto = {
      campaigns: [],
      totalBudget: 1000
    };

    service.uploadFile(mockFile).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/campaigns/upload`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should handle error response with message object', () => {
    const mockFile = new File([''], 'test.csv', { type: 'text/csv' });
    const errorMessage = 'Validation Error';
    
    service.uploadFile(mockFile).subscribe({
      next: () => fail('should have failed with the 400 error'),
      error: (error) => {
        expect(error).toBe(errorMessage);
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/campaigns/upload`);
    req.flush({ message: errorMessage }, { status: 400, statusText: 'Bad Request' });
  });

  it('should handle error response with string body', () => {
    const mockFile = new File([''], 'test.csv', { type: 'text/csv' });
    const errorMessage = 'String Error';
    
    service.uploadFile(mockFile).subscribe({
      next: () => fail('should have failed with the 400 error'),
      error: (error) => {
        expect(error).toBe(errorMessage);
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/campaigns/upload`);
    req.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
  });
});
