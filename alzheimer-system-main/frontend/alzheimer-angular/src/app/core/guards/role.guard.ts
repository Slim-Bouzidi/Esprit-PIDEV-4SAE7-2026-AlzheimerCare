import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import keycloak from '../../keycloak';

export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const requiredRole = route.data['role'] as string;

  if (!requiredRole) return true;

  const roleAliases: Record<string, string[]> = {
    ADMIN: ['ADMIN'],
    SOIGNANT: ['SOIGNANT', 'CAREGIVER'],
    MEDECIN: ['MEDECIN', 'DOCTOR', 'DOCTEUR'],
    AIDANT: ['AIDANT', 'PATIENT'],
  };

  const hasRole = (role: string) =>
    (roleAliases[role] || [role]).some(alias =>
      keycloak.hasRealmRole(alias) ||
      keycloak.hasResourceRole(alias) ||
      keycloak.hasRealmRole(alias.toUpperCase()) ||
      keycloak.hasResourceRole(alias.toUpperCase())
    );

  if (hasRole(requiredRole)) {
    return true;
  }

  router.navigate(['/']);
  return false;
};
