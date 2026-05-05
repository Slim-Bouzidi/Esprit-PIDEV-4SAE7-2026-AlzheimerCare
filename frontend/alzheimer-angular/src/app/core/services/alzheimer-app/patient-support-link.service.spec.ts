import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PatientSupportLinkService } from './patient-support-link.service';

describe('PatientSupportLinkService', () => {
  let service: PatientSupportLinkService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PatientSupportLinkService],
    });

    service = TestBed.inject(PatientSupportLinkService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('loads support-network patients from /api/support-patients', () => {
    const payload = [{ id: 1, fullName: 'Patient One' }];

    service.getPatients().subscribe((rows) => {
      expect(rows.length).toBe(1);
      expect(rows[0].id).toBe(1);
    });

    const req = httpMock.expectOne((r) => r.url.endsWith('/api/support-patients'));
    expect(req.request.method).toBe('GET');
    req.flush(payload);
  });

  it('syncSupportPatient maps idPatient and computed fullName', () => {
    service
      .syncSupportPatient({ idPatient: 12, firstName: 'Alice', lastName: 'Martin' })
      .subscribe((row) => {
        expect(row.id).toBe(12);
      });

    const req = httpMock.expectOne((r) => r.url.endsWith('/api/support-patients'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ id: 12, fullName: 'Alice Martin' });
    req.flush({ id: 12, fullName: 'Alice Martin' });
  });

  it('updates patient geo/name using PUT /api/support-patients/{id}', () => {
    service
      .updatePatient(5, { fullName: 'P', zone: 'A', latitude: 36.8, longitude: 10.1 })
      .subscribe((row) => {
        expect(row.id).toBe(5);
      });

    const req = httpMock.expectOne((r) => r.url.endsWith('/api/support-patients/5'));
    expect(req.request.method).toBe('PUT');
    expect(req.request.body.zone).toBe('A');
    req.flush({ id: 5, fullName: 'P', zone: 'A' });
  });
});

