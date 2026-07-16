import { HttpInterceptorFn } from '@angular/common/http';
import { AuthenticationService } from '../../service/authentication-service';
import { inject } from '@angular/core';

export const jwtInterceptor: HttpInterceptorFn = (request, next) => {

  const authenticationService = inject(AuthenticationService);
  const currentUser = authenticationService.currentUserValue;

  // Add authorization header with jwt token if available
  if (currentUser && currentUser.token) {
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${currentUser.token}`
      }
    });
  }

  return next(request);
};
