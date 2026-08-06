import { HttpInterceptorFn } from '@angular/common/http';
import { AuthenticationService } from '../../service/authentication-service';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CookieService } from 'ngx-cookie-service';

export const jwtInterceptor: HttpInterceptorFn = (request, next) => {

  // const platformId = inject(PLATFORM_ID);
  const cookieService = inject(CookieService);

  const currentUser = cookieService.get("userSession") ? JSON.parse(cookieService.get("userSession")) : null;
  // if (isPlatformBrowser(platformId)) {

    if (currentUser && currentUser.userName) {
      request = request.clone({
        withCredentials: true
      });
    }
  // }
  return next(request);

};
