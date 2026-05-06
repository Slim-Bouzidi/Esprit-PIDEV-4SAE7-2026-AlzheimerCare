import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import keycloak from '../../keycloak';

type KeycloakResourceAccessEntry = { roles?: string[] };

/**
 * App shell is only for logged-in users. Unauthenticated visitors are sent to /landing.
 * Doctors are sent to /doctor-dashboard instead of the patient shell.
 */
export const appShellGuard: CanActivateFn = (_route, state): boolean | UrlTree => {
  const router = inject(Router);

  if (!keycloak.authenticated) {
    return router.createUrlTree(['/landing']);
  }

  const realmRoles = keycloak.realmAccess?.roles || [];
  const resourceRoles = Object.values(keycloak.resourceAccess || {}).flatMap(
    (resource) => (resource as KeycloakResourceAccessEntry).roles || []
  );
  const tokenRoles =
    (keycloak.tokenParsed && 'roles' in keycloak.tokenParsed
      ? (keycloak.tokenParsed['roles'] as string[] | undefined)
      : []) || [];

  const allRoles = [...realmRoles, ...resourceRoles, ...tokenRoles].map((r) => r.toUpperCase());

  const isDoctor = allRoles.some((r) => r.includes('DOCTOR') || r.includes('DOCTEUR'));
  const isShellHomeRequest =
    state.url === '' ||
    state.url === '/' ||
    state.url === '/dashboard';

  if (isDoctor && isShellHomeRequest) {
    return router.createUrlTree(['/doctor-dashboard']);
  }

  return true;
};
