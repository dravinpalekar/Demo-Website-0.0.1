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

    if (currentUser?.token !== undefined) {
      // check if route is restricted by role
      const isSuperAdmin = currentUser.roles?.includes(Role.SuperAdmin) ?? false;

      if (route.data['roles'] && route.data['roles'].length > 0 && !isSuperAdmin) {
        // for normal user or guest user
        return true;
      }
      // authorized so return true
      // for admin and super-admin user
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
