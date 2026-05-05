import { FormBuilder } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { NEVER, of, throwError } from 'rxjs';
import type { DispatchPlan } from '../../../../network/models/support-network-advanced.types';

describe('PatientNetworkPageComponent', () => {
  beforeEach(() => {
    (globalThis as unknown as { global?: unknown }).global = globalThis;
  });

  async function createFixture(linksResponse = of([] as unknown[])) {
    const { PatientNetworkPageComponent } = await import('./patient-network-page.component');

    const membersService = {
      getAll: jasmine.createSpy('getAll').and.returnValue(of([{ id: 1, fullName: 'Member A' }])),
    };
    const linkService = {
      getPatients: jasmine.createSpy('getPatients').and.returnValue(of([{ id: 10, fullName: 'Pat A', zone: 'Z1' }])),
      getLinksByPatient: jasmine.createSpy('getLinksByPatient').and.returnValue(linksResponse),
      updatePatient: jasmine.createSpy('updatePatient').and.returnValue(of({ id: 10, fullName: 'Pat A', zone: 'Z2' })),
      createLink: jasmine.createSpy('createLink').and.returnValue(of({})),
      updateLink: jasmine.createSpy('updateLink').and.returnValue(of({})),
      deleteLink: jasmine.createSpy('deleteLink').and.returnValue(of(void 0)),
    };
    const engineApi = { getBestIntervenants: jasmine.createSpy('getBestIntervenants').and.returnValue(of([])) };
    const dispatchPlannerApi = {
      generatePlan: jasmine.createSpy('generatePlan').and.returnValue(of(null as DispatchPlan | null)),
    };
    const missionService = { dispatchMission: jasmine.createSpy('dispatchMission').and.returnValue(of({})) };
    const alertService = { triggerAlert: jasmine.createSpy('triggerAlert').and.returnValue(of({})) };
    const dispatchHistoryService = {
      getDispatchHistoryForPatient: jasmine.createSpy('getDispatchHistoryForPatient').and.returnValue(of([])),
      getDispatchHistoryDetail: jasmine.createSpy('getDispatchHistoryDetail').and.returnValue(of(null)),
    };
    const translate = { instant: (key: string) => key };
    const websocketService = {
      onMissionUpdate: () => of(null),
      onNotification: () => of(null),
      onDispatchUpdate: () => of(null),
      /** Avoid synchronous emission: component subscribes before storing the sub; `of(null)` would recurse via refreshDispatchHistory. */
      watchDispatch: jasmine.createSpy('watchDispatch').and.returnValue(NEVER),
    };

    const component = new PatientNetworkPageComponent(
      new FormBuilder(),
      membersService as never,
      linkService as never,
      engineApi as never,
      dispatchPlannerApi as never,
      missionService as never,
      alertService as never,
      dispatchHistoryService as never,
      translate as never,
      websocketService as never
    );

    return {
      component,
      membersService,
      linkService,
      dispatchPlannerApi,
      missionService,
      dispatchHistoryService,
    };
  }

  it('loads selector data and auto-selects first patient', async () => {
    const { component, linkService } = await createFixture();
    component.loadInitial();

    expect(component.patients.length).toBe(1);
    expect(component.selectedPatientId).toBe(10);
    expect(linkService.getLinksByPatient).toHaveBeenCalledWith(10);
  });

  it('updates selected patient geo via linkService.updatePatient', async () => {
    const { component, linkService } = await createFixture();
    component.loadInitial();
    component.patientGeoForm.patchValue({ zone: 'Z2', latitude: 36.8, longitude: 10.1 });

    component.savePatientGeo();

    expect(linkService.updatePatient).toHaveBeenCalledWith(
      10,
      jasmine.objectContaining({ zone: 'Z2', latitude: 36.8, longitude: 10.1 })
    );
  });

  it('create link: valid form calls createLink and shows success', async () => {
    const { component, linkService } = await createFixture(of([]));
    component.loadInitial();
    component.form.patchValue({
      memberId: 1,
      roleInNetwork: 'Family',
      trustLevel: 'TRUSTED',
      priorityRank: 1,
      permissions: [],
      canAccessHome: false,
    });

    component.onSubmit();

    expect(linkService.createLink).toHaveBeenCalledWith(
      jasmine.objectContaining({ patientId: 10, memberId: 1, roleInNetwork: 'Family', trustLevel: 'TRUSTED' })
    );
    expect(component.loading).toBe(false);
    expect(component.message?.type).toBe('success');
  });

  it('create link: blocks duplicate member client-side (no HTTP)', async () => {
    const existing = [{ id: 50, member: { id: 1 }, roleInNetwork: 'x', trustLevel: 'TRUSTED', priorityRank: 1 }];
    const { component, linkService } = await createFixture(of(existing));
    component.loadInitial();
    component.form.patchValue({
      memberId: 1,
      roleInNetwork: 'Family',
      trustLevel: 'TRUSTED',
      priorityRank: 1,
    });

    component.onSubmit();

    expect(linkService.createLink).not.toHaveBeenCalled();
    expect(component.message?.type).toBe('error');
    expect(component.message?.text).toContain('déjà');
  });

  it('edit link: calls updateLink with editing id', async () => {
    const rows = [{ id: 5, member: { id: 2 }, roleInNetwork: 'Old', trustLevel: 'TRUSTED', priorityRank: 1 }];
    const { component, linkService } = await createFixture(of(rows));
    component.loadInitial();
    component.editLink(component.links[0] as never);
    component.form.patchValue({ roleInNetwork: 'Caregiver' });

    component.onSubmit();

    expect(linkService.updateLink).toHaveBeenCalledWith(
      5,
      jasmine.objectContaining({ patientId: 10, memberId: 2, roleInNetwork: 'Caregiver' })
    );
    expect(component.message?.type).toBe('success');
  });

  it('edit link: blocks switching to another linked member', async () => {
    const rows = [
      { id: 5, member: { id: 2 }, roleInNetwork: 'a', trustLevel: 'TRUSTED', priorityRank: 1 },
      { id: 6, member: { id: 3 }, roleInNetwork: 'b', trustLevel: 'TRUSTED', priorityRank: 1 },
    ];
    const { component, linkService } = await createFixture(of(rows));
    component.loadInitial();
    component.editLink(component.links[0] as never);
    component.form.patchValue({ memberId: 3 });

    component.onSubmit();

    expect(linkService.updateLink).not.toHaveBeenCalled();
    expect(component.message?.text).toContain('déjà');
  });

  it('generateDispatchPlan: stores plan and clears error when steps returned', async () => {
    const linkRows = [{ id: 1, member: { id: 9 }, roleInNetwork: 'x', trustLevel: 'TRUSTED', priorityRank: 1 }];
    const { component, dispatchPlannerApi } = await createFixture(of(linkRows));
    component.loadInitial();
    const plan: DispatchPlan = {
      alertType: 'CHUTE',
      steps: [{ step: 1, timeoutMinutes: 5, timeoutLabel: '5 min', assignees: [] }],
    };
    dispatchPlannerApi.generatePlan.and.returnValue(of(plan));

    component.generateDispatchPlan();

    expect(component.dispatchPlanLoading).toBe(false);
    expect(component.dispatchPlanError).toBeNull();
    expect(component.dispatchPlan?.steps?.length).toBe(1);
    expect(dispatchPlannerApi.generatePlan).toHaveBeenCalled();
  });

  it('dispatchMissionForMember: clears row spinner after success', async () => {
    const linkRows = [{ id: 1, member: { id: 9 }, roleInNetwork: 'x', trustLevel: 'TRUSTED', priorityRank: 1 }];
    const { component, missionService } = await createFixture(of(linkRows));
    component.loadInitial();

    component.dispatchMissionForMember(9);

    expect(missionService.dispatchMission).toHaveBeenCalled();
    expect(component.missionDispatchMemberId).toBeNull();
    expect(component.message?.type).toBe('success');
  });

  it('loadDispatchHistory: fills items and clears loading', async () => {
    const { component, dispatchHistoryService } = await createFixture();
    component.loadInitial();
    dispatchHistoryService.getDispatchHistoryForPatient.and.returnValue(
      of([{ id: 77, patientId: 10, memberId: 1, alertType: 'CHUTE', status: 'OPEN', createdAt: '' }])
    );

    component.loadDispatchHistory();

    expect(component.dispatchHistoryLoading).toBe(false);
    expect(component.dispatchHistoryQueried).toBe(true);
    expect(component.dispatchHistoryItems.length).toBe(1);
    expect(component.dispatchHistoryError).toBeNull();
  });

  it('HTTP error: patient list load sets error message from backend body', async () => {
    const { component, linkService } = await createFixture();
    linkService.getPatients.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 503, error: { message: 'Patients unavailable' } }))
    );

    component.loadInitial();

    expect(component.loading).toBe(false);
    expect(component.message?.type).toBe('error');
    expect(component.message?.text).toBe('Patients unavailable');
  });

  it('HTTP error: savePatientGeo clears saving flag and surfaces message', async () => {
    const { component, linkService } = await createFixture();
    component.loadInitial();
    linkService.updatePatient.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { message: 'Invalid coordinates' } }))
    );
    component.patientGeoForm.patchValue({ latitude: 10, longitude: 20 });

    component.savePatientGeo();

    expect(component.patientGeoSaving).toBe(false);
    expect(component.message?.type).toBe('error');
    expect(component.message?.text).toBe('Invalid coordinates');
  });

  it('HTTP error: createLink failure clears loading and shows error', async () => {
    const { component, linkService } = await createFixture(of([]));
    component.loadInitial();
    linkService.createLink.and.returnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 409,
            error: { message: 'This member is already linked to this patient.' },
          })
      )
    );
    component.form.patchValue({
      memberId: 1,
      roleInNetwork: 'Family',
      trustLevel: 'TRUSTED',
      priorityRank: 1,
    });

    component.onSubmit();

    expect(component.loading).toBe(false);
    expect(component.message?.type).toBe('error');
    expect(component.message?.text).toContain('already linked');
  });

  it('HTTP error: generateDispatchPlan sets dispatchPlanError', async () => {
    const linkRows = [{ id: 1, member: { id: 9 }, roleInNetwork: 'x', trustLevel: 'TRUSTED', priorityRank: 1 }];
    const { component, dispatchPlannerApi } = await createFixture(of(linkRows));
    component.loadInitial();
    dispatchPlannerApi.generatePlan.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 500, error: { message: 'Planner down' } }))
    );

    component.generateDispatchPlan();

    expect(component.dispatchPlanLoading).toBe(false);
    expect(component.dispatchPlanError).toBe('Planner down');
  });

  it('HTTP error: loadDispatchHistory sets dispatchHistoryError', async () => {
    const { component, dispatchHistoryService } = await createFixture();
    component.loadInitial();
    dispatchHistoryService.getDispatchHistoryForPatient.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 502, error: { message: 'History gateway' } }))
    );

    component.loadDispatchHistory();

    expect(component.dispatchHistoryLoading).toBe(false);
    expect(component.dispatchHistoryQueried).toBe(true);
    expect(component.dispatchHistoryError).toBe('History gateway');
  });

  it('HTTP error: dispatch history detail load sets dispatchHistoryError', async () => {
    const { component, dispatchHistoryService } = await createFixture();
    component.loadInitial();
    dispatchHistoryService.getDispatchHistoryDetail.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 404, error: { message: 'Detail missing' } }))
    );

    component.openDispatchHistoryDetail(42);

    expect(component.dispatchHistoryDetailLoading).toBe(false);
    expect(component.dispatchHistoryError).toBe('Detail missing');
  });
});
