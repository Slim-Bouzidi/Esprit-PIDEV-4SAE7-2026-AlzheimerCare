import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import keycloak from '../keycloak';

export interface TokenResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
  refresh_expires_in: number;
  token_type: string;
  id_token: string;
  session_state: string;
  scope: string;
}

export interface LoginError {
  error: string;
  error_description: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenUrl = `${environment.keycloakUrl}/realms/${environment.keycloakRealm}/protocol/openid-connect/token`;
  private clientId = environment.keycloakClientId;

  constructor(private http: HttpClient) { }

  /**
   * Authenticate directly against Keycloak's token endpoint.
   */
  login(email: string, password: string): Observable<TokenResponse> {
    const body = new HttpParams()
      .set('grant_type', 'password')
      .set('client_id', this.clientId)
      .set('username', email)
      .set('password', password);

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    return this.http.post<TokenResponse>(this.tokenUrl, body.toString(), { headers }).pipe(
      catchError((error) => {
        if (error.status === 401 || error.status === 400) {
          return throwError(() => ({
            error: 'invalid_credentials',
            error_description: 'Invalid email or password.'
          } as LoginError));
        }
        return throwError(() => ({
          error: 'server_error',
          error_description: 'Authentication service is unavailable.'
        } as LoginError));
      })
    );
  }

  /**
   * Production-grade Keycloak Initialization.
   * This is called by APP_INITIALIZER in app.config.ts.
   */
  async initKeycloak(): Promise<void> {
    const savedToken = sessionStorage.getItem('kc_token');
    const savedRefreshToken = sessionStorage.getItem('kc_refresh_token');

    try {
      const authenticated = await keycloak.init({
        onLoad: undefined, // Non-blocking: allows landing page to render immediately
        checkLoginIframe: false,
        pkceMethod: 'S256',
        token: savedToken || undefined,
        refreshToken: savedRefreshToken || undefined
      });

      if (authenticated) {
        await this.syncUser();
      }
    } catch (e) {
      console.warn('Keycloak init skipped (Expected for Landing Page)', e);
    }
  }

  /**
   * Synchronize authenticated user with the backend user-service.
   */
  async syncUser(): Promise<void> {
    if (!keycloak.authenticated || !keycloak.tokenParsed) return;

    const tokenParsed = keycloak.tokenParsed as any;
    const keycloakId = tokenParsed.sub;
    const roles = tokenParsed.realm_access?.roles || [];
    const email = tokenParsed.email || `${tokenParsed.preferred_username || keycloakId}@keycloak-sync.local`;
    
    // Map roles to backend expectations
    const role = roles.includes('DOCTEUR') || roles.includes('DOCTOR') ? 'DOCTOR' :
                 roles.includes('ADMIN') ? 'ADMIN' : 'PATIENT';

    try {
      const response = await fetch(`${environment.apiUrl}/users/by-keycloak-id/${keycloakId}`, {
        headers: { Authorization: `Bearer ${keycloak.token}` }
      });

      if (response.status === 404) {
        await fetch(`${environment.apiUrl}/users/sync`, {
          method: 'POST',
          headers: { 
            'Content-Type': 'application/json',
            Authorization: `Bearer ${keycloak.token}` 
          },
          body: JSON.stringify({ keycloakId, email, role })
        });
      }
    } catch (e) {
      console.warn('User synchronization failed', e);
    }
  }
}
