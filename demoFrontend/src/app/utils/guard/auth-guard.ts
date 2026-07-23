import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthenticationService, Role } from '../../service/authentication-service';
import { allRoutes } from '../allRoutes/allRoutes';
import { isPlatformBrowser } from '@angular/common';

export const authGuard: CanActivateFn = (route, state) => {
  const platformId = inject(PLATFORM_ID);
  const isBrowser = isPlatformBrowser(platformId);

  if (isPlatformBrowser(platformId)) {
    const authenticationService = inject(AuthenticationService);
    const router = inject(Router);
    const currentUser = authenticationService.currentUserValue;

    // 1. Check ki user logged in hai ya nahi
    if (currentUser && currentUser.token) {
      const routeRoles = route.data['roles'] as Role[] | undefined;

      // Explicitly cast user roles to Role[]
      const userRoles = (currentUser.roles as Role) || [];

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
