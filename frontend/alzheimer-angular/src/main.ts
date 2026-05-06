import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import keycloak from './app/keycloak';
import { environment } from './environments/environment';

// Some CommonJS browser bundles (ex: SockJS/STOMP chain) still reference Node's `global`.
// Map it to `window` in browser runtime to avoid "ReferenceError: global is not defined".
(window as unknown as { global?: Window & typeof globalThis }).global = window;

/** Map realm roles to user-service UserSyncRequest allowed values (ADMIN|DOCTOR|CAREGIVER|PATIENT). */
function mapRealmRoleToSyncRole(roles: string[]): string {
  const normalized = roles.map((r) => (r === 'DOCTEUR' ? 'DOCTOR' : r));
  const allowed = ['ADMIN', 'DOCTOR', 'CAREGIVER', 'PATIENT'] as const;
  return normalized.find((r) => allowed.includes(r as (typeof allowed)[number])) || 'PATIENT';
}

/** user-service @Email + @NotBlank — derive a stable synthetic address if the token has no email. */
function resolveSyncEmail(tokenParsed: Record<string, unknown>, keycloakId: string): string {
  const raw = tokenParsed['email'];
  if (typeof raw === 'string' && raw.includes('@')) {
    return raw;
  }
  const preferred = tokenParsed['preferred_username'];
  if (typeof preferred === 'string' && preferred.includes('@')) {
    return preferred;
  }
  const local =
    (typeof preferred === 'string' ? preferred : keycloakId).replace(/[^a-zA-Z0-9._-]/g, '_') || 'user';
  return `${local}@keycloak-sync.local`;
}

// User sync function
async function syncUserIfNeeded() {
  if (!keycloak.authenticated || !keycloak.tokenParsed) {
    return;
  }

  const tokenParsed = keycloak.tokenParsed as Record<string, unknown>;
  const keycloakId = keycloak.tokenParsed.sub as string;
  const roles = (keycloak.tokenParsed['realm_access'] as { roles?: string[] } | undefined)?.roles || [];
  const userRole = mapRealmRoleToSyncRole(roles);
  const email = resolveSyncEmail(tokenParsed, keycloakId);

  try {
    const usersApiBaseUrl = `${environment.apiUrl}/users`;

    // Check if user exists in database
    const response = await fetch(
      `${usersApiBaseUrl}/by-keycloak-id/${encodeURIComponent(keycloakId)}`,
      {
        headers: {
          Authorization: `Bearer ${keycloak.token}`,
        },
      }
    );

    if (response.status === 404) {
      const syncRes = await fetch(`${usersApiBaseUrl}/sync`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${keycloak.token}`,
        },
        body: JSON.stringify({
          keycloakId,
          email,
          role: userRole,
        }),
      });
      if (!syncRes.ok) {
        const text = await syncRes.text().catch(() => '');
        console.warn('User sync failed:', syncRes.status, text);
      }
    }
  } catch (error) {
    console.error('Error syncing user:', error);
  }
}

const savedToken = sessionStorage.getItem('kc_token');
const savedRefreshToken = sessionStorage.getItem('kc_refresh_token');
const savedIdToken = sessionStorage.getItem('kc_id_token');

keycloak.init({
  onLoad: 'check-sso',
  silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
  checkLoginIframe: false,
  pkceMethod: 'S256',
  token: savedToken || undefined,
  refreshToken: savedRefreshToken || undefined,
  idToken: savedIdToken || undefined
}).then(async (authenticated) => {
  if (authenticated) {
    await syncUserIfNeeded();
  }

  bootstrapApplication(AppComponent, appConfig)
    .catch((err) => console.error(err));
}).catch((error) => console.error('Keycloak init failed', error));
