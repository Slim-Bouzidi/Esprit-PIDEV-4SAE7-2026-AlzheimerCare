import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MembersService } from './members.service';

describe('MembersService', () => {
  let service: MembersService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MembersService],
    });

    service = TestBed.inject(MembersService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('creates member with trimmed email and skills body', () => {
    service
      .create({
        fullName: 'John',
        type: 'FAMILY',
        email: ' john@site.com ',
        skills: ['DOCTOR'],
      } as any)
      .subscribe((member) => {
        expect(member.fullName).toBe('John');
      });

    const req = httpMock.expectOne((r) => r.url.endsWith('/api/members'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body.email).toBe('john@site.com');
    expect(req.request.body.skills).toEqual(['DOCTOR']);
    req.flush({ id: 1, fullName: 'John' });
  });

  it('updates member through PUT /api/members/{id}', () => {
    service.update(9, { fullName: 'Jane', type: 'NURSE' } as any).subscribe();

    const req = httpMock.expectOne((r) => r.url.endsWith('/api/members/9'));
    expect(req.request.method).toBe('PUT');
    expect(req.request.body.fullName).toBe('Jane');
    req.flush({ id: 9, fullName: 'Jane' });
  });
});

