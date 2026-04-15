import { HttpInterceptorFn } from '@angular/common/http';
// import keycloak from './keycloak'; // TEMPORARY: Disabled

/**
 * TEMPORARY: Keycloak disabled - not attaching JWT tokens
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // TEMPORARY: Bypass Keycloak token attachment (silent mode)
  return next(req);

  /* ORIGINAL CODE (commented out):
  const token = keycloak.token;

  if (token) {
    if (req.url.includes('cognitive-activities')) {
      console.log('Attaching JWT to cognitive-service request:', req.url);
    }
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
  */
};
