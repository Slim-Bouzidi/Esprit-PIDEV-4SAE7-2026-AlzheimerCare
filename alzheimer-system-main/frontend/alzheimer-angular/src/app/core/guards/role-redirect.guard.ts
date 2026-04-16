import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import keycloak from '../../keycloak';

/**
 * Guard that redirects the user to the appropriate dashboard
 * based on their Keycloak realm role.
 */
export const roleRedirectGuard: CanActivateFn = () => {
  const router = inject(Router);

  if (keycloak.hasRealmRole('ADMIN')) {
    router.navigate(['/admin']);
    return false;
  }

  if (keycloak.hasRealmRole('SOIGNANT') || keycloak.hasRealmRole('CAREGIVER')) {
    router.navigate(['/soignant-dashboard']);
    return false;
  }

  if (
    keycloak.hasRealmRole('MEDECIN') ||
    keycloak.hasRealmRole('DOCTOR') ||
    keycloak.hasRealmRole('DOCTEUR')
  ) {
    router.navigate(['/doctor-dashboard']);
    return false;
  }

  if (keycloak.hasRealmRole('AIDANT') || keycloak.hasRealmRole('PATIENT')) {
    router.navigate(['/aidant-dashboard']);
    return false;
  }

  console.warn('[RoleRedirect] No recognized role found, redirecting to admin by default');
  router.navigate(['/admin']);
  return false;
};
