import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import keycloak from '../../keycloak';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);

  if (keycloak.authenticated) {
    return true;
  }

  // Redirect to landing page if not authenticated
  return router.createUrlTree(['/landing']);
};
