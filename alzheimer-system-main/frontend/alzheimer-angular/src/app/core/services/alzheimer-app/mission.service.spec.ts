import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MissionService } from './mission.service';

describe('MissionService', () => {
  let service: MissionService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MissionService],
    });

    service = TestBed.inject(MissionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('dispatches mission to /api/missions/dispatch', () => {
    const payload = {
      patientId: 5,
      assignedMemberId: 7,
      alertType: 'MALAISE',
      title: 'Intervention',
      description: 'From test',
    };

    service.dispatchMission(payload as any).subscribe((mission) => {
      expect(mission.id).toBe(99);
    });

    const req = httpMock.expectOne((r) => r.url.endsWith('/api/missions/dispatch'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body.patientId).toBe(5);
    req.flush({ id: 99, status: 'PENDING' });
  });

  it('accepts mission using PATCH /api/missions/{id}/accept', () => {
    service.acceptMission(44).subscribe((mission) => {
      expect(mission.status).toBe('ACCEPTED');
    });

    const req = httpMock.expectOne((r) => r.url.endsWith('/api/missions/44/accept'));
    expect(req.request.method).toBe('PATCH');
    req.flush({ id: 44, status: 'ACCEPTED' });
  });
});

