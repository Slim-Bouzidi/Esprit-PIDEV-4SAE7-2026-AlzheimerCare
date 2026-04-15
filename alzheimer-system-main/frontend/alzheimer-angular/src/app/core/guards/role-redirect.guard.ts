import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
// import keycloak from '../../keycloak'; // TEMPORARY: Disabled

/**
 * Guard that redirects the user to the appropriate dashboard
 * based on their Keycloak realm role.
 * 
 * TEMPORARY: Keycloak disabled - COMPLETELY BYPASSED
 */
export const roleRedirectGuard: CanActivateFn = () => {
  // COMPLETELY DISABLED - Always allow navigation
  console.log('[RoleRedirect] GUARD BYPASSED - Navigation allowed');
  return true;

  /* ORIGINAL CODE (commented out):
  console.log('[RoleRedirect] Keycloak roles:', keycloak.realmAccess?.roles);

  // Check realm roles from Keycloak token — order matters (ADMIN first)
  if (keycloak.hasRealmRole('ADMIN')) {
    router.navigate(['/admin']);
    return false;
  }

  if (keycloak.hasRealmRole('SOIGNANT')) {
    router.navigate(['/soignant-dashboard']);
    return false;
  }

  if (keycloak.hasRealmRole('MEDECIN')) {
    router.navigate(['/doctor-dashboard']);
    return false;
  }

  if (keycloak.hasRealmRole('AIDANT')) {
    router.navigate(['/aidant-dashboard']);
    return false;
  }

  // Fallback: if no recognized role, redirect to admin (or show error)
  console.warn('[RoleRedirect] No recognized role found, redirecting to admin by default');
  router.navigate(['/admin']);
  return false;
  */
};
