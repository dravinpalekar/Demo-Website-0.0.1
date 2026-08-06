import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Role } from '../../service/authentication-service';
import { allRoutes } from '../allRoutes/allRoutes';
import { isPlatformBrowser } from '@angular/common';
import { CookieService } from 'ngx-cookie-service';

export const authGuard: CanActivateFn = (route, state) => {
  const platformId = inject(PLATFORM_ID);

  if (isPlatformBrowser(platformId)) {
    const cookieService = inject(CookieService);
    const router = inject(Router);
    const currentUser =  cookieService.get("userSession") ? JSON.parse(cookieService.get("userSession")): null;
   

    // 1. Check ki user logged in hai ya nahi
    if (currentUser && currentUser.userName) {
      const routeRoles = route.data['roles'] as Role[] | undefined;

      // Explicitly cast user roles to Role[]
      const userRoles = (currentUser.roles) || [];

      if (routeRoles && routeRoles.length > 0) {
        // Ab userRoles.includes(role) bina kisi TypeScript error ke chalega
        const hasRequiredRole = routeRoles.some((role: Role) => userRoles.includes(role));

        if (!hasRequiredRole) {
          // Access Denied: Route allow nahi hai toh Page Not Found ya Dashboard par redirect karein
          return router.createUrlTree([allRoutes.notFound]);
        }
      }

      return true;
    }

    // not logged in so redirect to login page with the return url
    if (state.url.includes(allRoutes.superAdminDashboard)) {
      // for admin and super-admin user
      return router.createUrlTree([allRoutes.superAdminLogin], { queryParams: { returnUrl: state.url } });
    }
    else {
      // for normal user or guest user
      return router.createUrlTree([allRoutes.login], { queryParams: { returnUrl: state.url } });
    }
  }
  return true;
};
