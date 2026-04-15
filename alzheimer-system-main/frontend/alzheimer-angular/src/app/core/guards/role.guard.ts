import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
// import keycloak from '../../keycloak'; // TEMPORARY: Disabled

/**
 * TEMPORARY: Keycloak disabled - allowing all routes
 */
export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const requiredRole = route.data['role'] as string;

  // TEMPORARY: Bypass Keycloak and allow all routes
  console.warn('[RoleGuard] Keycloak disabled - allowing access to:', state.url);
  return true;

  /* ORIGINAL CODE (commented out):
  if (!requiredRole) return true;

  const hasRole = (role: string) => 
    keycloak.hasRealmRole(role) || 
    keycloak.hasResourceRole(role) || 
    keycloak.hasRealmRole(role.toUpperCase()) || 
    keycloak.hasResourceRole(role.toUpperCase());

  if (hasRole(requiredRole)) {
    return true;
  }

  // Redirect to home if unauthorized
  router.navigate(['/']);
  return false;
  */
};
