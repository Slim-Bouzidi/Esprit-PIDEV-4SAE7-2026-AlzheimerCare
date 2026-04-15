import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { InsightService } from './insight.service';
import { environment } from '../../environments/environment';

describe('InsightService', () => {
  let service: InsightService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(InsightService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch patient insights', () => {
    const mockInsights = [
      { id: 1, message: 'Insight', type: 'INFO', dateCreation: '2026-01-01T10:00:00' }
    ] as any;

    service.getInsights(5).subscribe((data) => {
      expect(data.length).toBe(1);
      expect(data[0].message).toBe('Insight');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/insights/patient/5`);
    expect(req.request.method).toBe('GET');
    req.flush(mockInsights);
  });

  it('should post interaction with optional details', () => {
    service.recordInteraction(4, 'FAILURE', 'test-detail').subscribe();

    const req = httpMock.expectOne((r) =>
      r.url === `${environment.apiUrl}/insights/interaction` &&
      r.params.get('patientId') === '4' &&
      r.params.get('type') === 'FAILURE' &&
      r.params.get('details') === 'test-detail'
    );
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should surface HTTP errors when insights request fails', (done) => {
    service.getInsights(2).subscribe({
      next: () => fail('expected error'),
      error: (err) => {
        expect(err.status).toBe(503);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/insights/patient/2`);
    req.flush('Service unavailable', { status: 503, statusText: 'Service Unavailable' });
  });
});
