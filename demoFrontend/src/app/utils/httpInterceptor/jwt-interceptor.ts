import { HttpInterceptorFn } from '@angular/common/http';
import { AuthenticationService } from '../../service/authentication-service';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export const jwtInterceptor: HttpInterceptorFn = (request, next) => {

  const platformId = inject(PLATFORM_ID);
  const authenticationService = inject(AuthenticationService);

  if (isPlatformBrowser(platformId)) {
    const currentUser = authenticationService.currentUserValue;

    // Add authorization header with jwt token if available
    if (currentUser && currentUser.token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${currentUser.token}`
        }
      });
    }
  }
  return next(request);
};
