import { of, throwError } from 'rxjs';
import { DoctorReportsComponent } from './doctor-reports.component';

describe('DoctorReportsComponent', () => {
  const routerMock = { navigate: jasmine.createSpy('navigate') } as any;
  const rapportServiceMock = {
    getAll: jasmine.createSpy('getAll').and.returnValue(of([])),
    delete: jasmine.createSpy('delete').and.returnValue(of(void 0)),
    envoyer: jasmine.createSpy('envoyer').and.returnValue(of({}))
  } as any;
  const patientServiceMock = { getAll: jasmine.createSpy('getAll').and.returnValue(of([])) } as any;
  const rapportSuiviServiceMock = { getRapportsSuiviStructure: jasmine.createSpy('getRapportsSuiviStructure').and.returnValue([]) } as any;
  const notificationServiceMock = {
    getByUser: jasmine.createSpy('getByUser').and.returnValue(of([])),
    getUnreadCount: jasmine.createSpy('getUnreadCount').and.returnValue(of({ count: 0 }))
  } as any;
  const rapportHebdoApiMock = {
    getAll: jasmine.createSpy('getAll').and.returnValue(of([])),
    marquerConsulte: jasmine.createSpy('marquerConsulte').and.returnValue(of({}))
  } as any;
  const ficheApiMock = { getAll: jasmine.createSpy('getAll').and.returnValue(of([])) } as any;
  const doctorWsMock = { connect: jasmine.createSpy('connect'), disconnect: jasmine.createSpy('disconnect'), notifications$: of() } as any;
  const httpMock = { get: jasmine.createSpy('get') } as any;
  const translateMock = { instant: jasmine.createSpy('instant').and.callFake((k: string) => k) } as any;
  const insightServiceMock = {
    getInsights: jasmine.createSpy('getInsights').and.returnValue(of([{ message: 'i', type: 'INFO', dateCreation: '2026-01-01' }])),
    triggerGlobalAnalysis: jasmine.createSpy('triggerGlobalAnalysis').and.returnValue(of(void 0))
  } as any;
  const cdrMock = { detectChanges: jasmine.createSpy('detectChanges') } as any;
  const ngZoneMock = { run: (fn: any) => fn() } as any;

  beforeEach(() => {
    insightServiceMock.getInsights.calls.reset();
    insightServiceMock.triggerGlobalAnalysis.calls.reset();
    rapportHebdoApiMock.marquerConsulte.calls.reset();
  });

  function createComponent() {
    return new DoctorReportsComponent(
      routerMock,
      rapportServiceMock,
      patientServiceMock,
      rapportSuiviServiceMock,
      notificationServiceMock,
      rapportHebdoApiMock,
      ficheApiMock,
      doctorWsMock,
      httpMock,
      translateMock,
      insightServiceMock,
      cdrMock,
      ngZoneMock
    );
  }

  it('should load insights when selecting a weekly report with nested patient.id', () => {
    const component = createComponent();
    const hebdo = { id: 1, patient: { id: 7 }, consulteParMedecin: true };

    component.consulterHebdo(hebdo);

    expect(insightServiceMock.getInsights).toHaveBeenCalledWith(7);
    expect(component.loadingInsights).toBeFalse();
  });

  it('should avoid calling insights API when patient id cannot be resolved', () => {
    const component = createComponent();
    const hebdo = { id: 1, patient: null, consulteParMedecin: true };

    component.consulterHebdo(hebdo);

    expect(insightServiceMock.getInsights).not.toHaveBeenCalled();
    expect(component.insights).toEqual([]);
  });

  it('should stop loadingInsights on insight API failure', () => {
    const component = createComponent();
    insightServiceMock.getInsights.and.returnValue(throwError(() => new Error('network')));

    component.loadInsights(9);

    expect(component.loadingInsights).toBeFalse();
  });

  it('should populate insights when API returns data', () => {
    insightServiceMock.getInsights.and.returnValue(
      of([{ message: 'Analyse', type: 'INFO', dateCreation: '2026-01-01T00:00:00' }])
    );
    const component = createComponent();
    component.loadInsights(3);
    expect(component.insights.length).toBe(1);
    expect(component.insights[0].message).toBe('Analyse');
    expect(component.loadingInsights).toBeFalse();
  });

  it('should leave insights empty when API returns no rows', () => {
    insightServiceMock.getInsights.and.returnValue(of([]));
    const component = createComponent();
    component.loadInsights(4);
    expect(component.insights).toEqual([]);
    expect(component.loadingInsights).toBeFalse();
  });

  it('should clear insights when patient id is missing', () => {
    const component = createComponent();
    component.insights = [{ message: 'x', type: 'INFO', dateCreation: '2026-01-01' } as any];
    component.loadInsights(0);
    expect(component.insights).toEqual([]);
  });
});
