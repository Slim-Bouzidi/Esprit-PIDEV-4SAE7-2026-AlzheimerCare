import { HttpInterceptorFn } from '@angular/common/http';
import { from, switchMap, catchError, throwError } from 'rxjs';
import keycloak from './keycloak';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Skip token update for public endpoints
  if (req.url.includes('/register') || req.url.includes('/login')) {
    return next(req);
  }

  // Refresh the token if it will expire in the next 30 seconds
  return from(keycloak.updateToken(30)).pipe(
    switchMap(() => {
      const token = keycloak.token;
      if (token) {
        req = req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`
          }
        });
      }
      return next(req);
    }),
    catchError((error) => {
      if (error.status === 401) {
        keycloak.login();
      }
      return throwError(() => error);
    })
  );
};
