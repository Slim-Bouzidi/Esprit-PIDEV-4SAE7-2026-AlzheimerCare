import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { supportNetworkHttpHeaders } from '../../support-network-headers';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  NetworkPatient,
  PatientSupportLink,
  LinkCreateDto,
  SupportNetworkPatientUpdateDto,
} from '../../models/alzheimer-app/patient-network.model';

const BASE = (environment as { supportNetworkApiUrl?: string }).supportNetworkApiUrl ?? '/api';

@Injectable({ providedIn: 'root' })
export class PatientSupportLinkService {
  private api = BASE;

  constructor(private http: HttpClient) {}

  /** GET /api/support-patients — support-network PatientController (not patient-service /api/patients). */
  getPatients(): Observable<NetworkPatient[]> {
    console.log('Calling API:', `${this.api}/support-patients`);
    return this.http.get<NetworkPatient[]>(`${this.api}/support-patients`, {
      headers: supportNetworkHttpHeaders(),
    });
  }

  /**
   * POST /api/support-patients — mirrors a patient from doctor/admin flow into support-network store.
   * This does not change main patient-service data; it only ensures selector visibility.
   */
  syncSupportPatient(patient: {
    id?: number;
    idPatient?: number;
    fullName?: string;
    firstName?: string;
    lastName?: string;
  }): Observable<NetworkPatient> {
    const id = patient.idPatient ?? patient.id;
    const fullName =
      (patient.fullName && patient.fullName.trim()) ||
      `${patient.firstName ?? ''} ${patient.lastName ?? ''}`.trim() ||
      `Patient #${id ?? ''}`.trim();
    return this.http.post<NetworkPatient>(
      `${this.api}/support-patients`,
      { id, fullName },
      { headers: supportNetworkHttpHeaders() }
    );
  }

  /** PUT /api/support-patients/{id} — update name, zone, WGS84 (PatientCreateDto on backend). */
  updatePatient(id: number, body: SupportNetworkPatientUpdateDto): Observable<NetworkPatient> {
    console.log('Calling API:', `${this.api}/support-patients/${id}`);
    return this.http.put<NetworkPatient>(`${this.api}/support-patients/${id}`, body, {
      headers: supportNetworkHttpHeaders(),
    });
  }

  /** GET /api/network/patient/{patientId} - links for patient */
  getLinksByPatient(patientId: number): Observable<PatientSupportLink[]> {
    console.log('Calling API:', `${this.api}/network/patient/${patientId}`);
    return this.http.get<PatientSupportLink[]>(
      `${this.api}/network/patient/${patientId}`,
      { headers: supportNetworkHttpHeaders() }
    );
  }

  /** POST /api/network/link - create link (body: LinkCreateDto) */
  createLink(dto: LinkCreateDto): Observable<PatientSupportLink> {
    console.log('Calling API:', `${this.api}/network/link`);
    return this.http.post<PatientSupportLink>(
      `${this.api}/network/link`,
      dto,
      { headers: supportNetworkHttpHeaders() }
    );
  }

  /** PUT /api/network/{linkId} - update link (body: same LinkCreateDto as create) */
  updateLink(linkId: number, dto: LinkCreateDto): Observable<PatientSupportLink> {
    console.log('Calling API:', `${this.api}/network/${linkId}`);
    return this.http.put<PatientSupportLink>(
      `${this.api}/network/${linkId}`,
      dto,
      { headers: supportNetworkHttpHeaders() }
    );
  }

  /** DELETE /api/network/{linkId} */
  deleteLink(linkId: number): Observable<void> {
    console.log('Calling API:', `${this.api}/network/${linkId}`);
    return this.http.delete<void>(`${this.api}/network/${linkId}`, {
      headers: supportNetworkHttpHeaders(),
    });
  }
}
