import { HttpErrorResponse, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { AuthenticationService } from '../../service/authentication-service';
import { inject } from '@angular/core';
import { BehaviorSubject, catchError, filter, switchMap, take, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { allRoutes } from '../allRoutes/allRoutes';

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<any>(null);

export const jwtInterceptor: HttpInterceptorFn = (request: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);

  // Send credentials (cookies) with all outgoing HTTP requests
  const authReq = request.clone({
    withCredentials: true
  });

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // If 401 Unauthorized occurs on an API call, and it is NOT an authentication endpoint
      if (error.status === 401 && !request.url.includes(allRoutes.signIn) && !request.url.includes(allRoutes.signUp) && !request.url.includes(allRoutes.refreshToken) && !request.url.includes(allRoutes.logOut)) {
        return handle401Error(authReq, next, authService, router);
      }

      return throwError(() => error);
    })
  );
};

function handle401Error(request: HttpRequest<unknown>, next: HttpHandlerFn, authService: AuthenticationService, router: Router) {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    return authService.refreshToken().pipe(
      switchMap((res: any) => {
        isRefreshing = false;
        refreshTokenSubject.next(res);
        // Retry the original request
        return next(request);
      }),
      catchError((err) => {
        isRefreshing = false;
        refreshTokenSubject.next(null);
        authService.logout().subscribe();
        if (router.url.includes(allRoutes.superAdminDashboard)) {
          router.navigate([allRoutes.superAdminLogin]);
        } else {
          router.navigate([allRoutes.login]);
        }
        return throwError(() => err);
      })
    );
  } else {
    // If a refresh is already in progress, wait for it to complete then retry request
    return refreshTokenSubject.pipe(
      filter((token) => token !== null),
      take(1),
      switchMap(() => next(request))
    );
  }
}
