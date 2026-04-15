import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RapportService } from './rapport.service';
import { environment } from '../../environments/environment';

describe('RapportService', () => {
  let service: RapportService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.setItem('token', 'test-token');
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(RapportService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.removeItem('token');
  });

  it('should call GET /rapports with auth header', () => {
    service.getAll().subscribe((items) => {
      expect(items).toEqual([]);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/rapports`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush([]);
  });

  it('should call PATCH envoyer endpoint', () => {
    service.envoyer(12).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/rapports/12/envoyer`);
    expect(req.request.method).toBe('PATCH');
    req.flush({});
  });

  it('should handle empty list from getAll', () => {
    service.getAll().subscribe((items) => {
      expect(items).toEqual([]);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/rapports`);
    req.flush([]);
  });

  it('should surface HTTP failure on getAll', (done) => {
    service.getAll().subscribe({
      next: () => fail('expected error'),
      error: (err) => {
        expect(err.status).toBe(401);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/rapports`);
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
  });
});
