import { HttpErrorResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';

describe('MyMissionsPageComponent', () => {
  beforeEach(() => {
    (globalThis as unknown as { global?: unknown }).global = globalThis;
  });

  async function createFixture() {
    const { MyMissionsPageComponent } = await import('./my-missions-page.component');

    const missionService = {
      getMyMissions: jasmine.createSpy('getMyMissions').and.returnValue(of([])),
      acceptMission: jasmine.createSpy('acceptMission').and.returnValue(of(void 0)),
      completeMission: jasmine.createSpy('completeMission').and.returnValue(of(void 0)),
      dispatchMission: jasmine.createSpy('dispatchMission').and.returnValue(of({})),
    };
    const membersService = {
      getAll: jasmine.createSpy('getAll').and.returnValue(of([{ id: 11, fullName: 'Intervenant A' }])),
    };
    const translate = { instant: (key: string) => key };
    const reportService = {
      createReport: jasmine.createSpy('createReport').and.returnValue(of({})),
      listForMission: jasmine.createSpy('listForMission').and.returnValue(of([])),
    };
    const cdr = { markForCheck: jasmine.createSpy('markForCheck') };
    const websocketService = {
      watchMissions: jasmine.createSpy('watchMissions'),
      watchNotifications: jasmine.createSpy('watchNotifications'),
      onMissionUpdate: () => of(null),
      onNotification: () => of(null),
    };

    const component = new MyMissionsPageComponent(
      missionService as never,
      membersService as never,
      translate as never,
      reportService as never,
      cdr as never,
      websocketService as never
    );

    return { component, missionService, membersService };
  }

  it('ngOnInit loads members and selects first id', async () => {
    const { component, membersService } = await createFixture();

    component.ngOnInit();

    expect(membersService.getAll).toHaveBeenCalled();
    expect(component.members.length).toBe(1);
    expect(component.selectedMemberId).toBe(11);
    expect(component.loading).toBe(false);
    component.ngOnDestroy();
  });

  it('loadMissions fills list and marks missionsLoaded', async () => {
    const { component, missionService } = await createFixture();
    missionService.getMyMissions.and.returnValue(
      of([
        {
          id: 501,
          patientId: 1,
          assignedMemberId: 11,
          alertType: 'CHUTE',
          title: 'Mission test',
          status: 'PENDING',
          createdAt: '',
          stepNumber: 1,
        },
      ])
    );

    component.ngOnInit();
    component.loadMissions();

    expect(component.missions.length).toBe(1);
    expect(component.missions[0].id).toBe(501);
    expect(component.missionsLoaded).toBe(true);
    expect(component.listLoading).toBe(false);
    component.ngOnDestroy();
  });

  it('loadMissions error clears rows and surfaces message', async () => {
    const { component, missionService } = await createFixture();
    missionService.getMyMissions.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 503, error: { message: 'Missions DB offline' } }))
    );

    component.ngOnInit();
    component.loadMissions();

    expect(component.missions.length).toBe(0);
    expect(component.missionsLoaded).toBe(true);
    expect(component.listLoading).toBe(false);
    expect(component.message?.type).toBe('error');
    expect(component.message?.text).toBe('Missions DB offline');
    component.ngOnDestroy();
  });

  it('accept clears actionMissionId on HTTP failure', async () => {
    const { component, missionService } = await createFixture();
    missionService.acceptMission.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 409, error: { message: 'Mission locked' } }))
    );

    component.ngOnInit();
    component.actionMissionId = 501;
    component.accept({
      id: 501,
      patientId: 1,
      assignedMemberId: 11,
      alertType: 'CHUTE',
      title: 't',
      status: 'PENDING',
      createdAt: '',
    });

    expect(component.actionMissionId).toBeNull();
    expect(component.message?.type).toBe('error');
    expect(component.message?.text).toBe('Mission locked');
    component.ngOnDestroy();
  });
});
