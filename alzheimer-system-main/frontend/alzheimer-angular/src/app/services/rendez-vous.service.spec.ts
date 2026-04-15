import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RendezVousService } from './rendez-vous.service';
import { environment } from '../../environments/environment';

describe('RendezVousService', () => {
  let service: RendezVousService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(RendezVousService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch appointments by date range', () => {
    service.getByDateRange('2026-04-01', '2026-04-30').subscribe((items) => {
      expect(items.length).toBe(0);
    });

    const req = httpMock.expectOne(
      `${environment.apiUrl}/rendez-vous/periode?debut=2026-04-01&fin=2026-04-30`
    );
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should confirm appointment', () => {
    service.confirmer(9).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/rendez-vous/9/confirmer`);
    expect(req.request.method).toBe('PUT');
    req.flush({});
  });

  it('should propagate errors when appointment list request fails', (done) => {
    service.getByDateRange('2026-01-01', '2026-01-31').subscribe({
      next: () => fail('expected error'),
      error: (err) => {
        expect(err.status).toBe(404);
        done();
      }
    });

    const req = httpMock.expectOne(
      `${environment.apiUrl}/rendez-vous/periode?debut=2026-01-01&fin=2026-01-31`
    );
    req.flush('Not found', { status: 404, statusText: 'Not Found' });
  });
});
