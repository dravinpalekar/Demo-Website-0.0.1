import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthenticationAuthorizationService, Role } from '../../service/authentication-authorization-service';
import { Observable } from 'rxjs';
import { allRoutes } from '../allRoutes/allRoutes';
import { inject, Injectable } from '@angular/core';

// @Injectable({
//   providedIn: 'root'
// })


// export class authGuardGuard{
// constructor(private router: Router, private authenticationService: AuthenticationAuthorizationService) { }

//   canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {

//     const currentUser = this.authenticationService.currentUserValue;
//     if (currentUser?.token !== undefined) {  
//       // check if route is restricted by role
//      const isSuperAdmin = currentUser.roles?.includes(Role.SuperAdmin) ?? false;

//       if (route.data['roles'] && route.data['roles'].length > 0 && !isSuperAdmin) {
//         // role not authorized so redirect to home page
//         this.router.navigate([allRoutes.superAdminLogin]);
//         return false;
//       }

      
      

//       // authorized so return true
//       return true;
//     }

//     // not logged in so redirect to login page with the return url
//     if(state.url.includes(allRoutes.superAdminDashboard))
//     {
//       this.router.navigate([allRoutes.superAdminLogin]);
//       return false;
//     }
//     else
//     {
//       this.router.navigate([allRoutes.login], { queryParams: { returnUrl: state.url } });
//       return false;
//     }
//     // throw new Error('Method not implemented.');
//     // return false;
//   }
// }

// // 2. Login Guard: Prevents logged-in users from seeing the login page (Eliminates the flash!)
// export const superAdminLoginGuard: CanActivateFn = () => {
//   const authService = inject(AuthenticationAuthorizationService);
//   const router = inject(Router);

//   const currentUser = authService.currentUserValue;

//   if (currentUser && currentUser.token) {
//     // Already logged in! Instantly bounce them to the dashboard
//     router.navigate([allRoutes.superAdminDashboard]);
//     return false;
//   }
//   return true;
// };

export const authGuardGuard: CanActivateFn = (route, state) => {

  const authenticationService = inject(AuthenticationAuthorizationService);
  const router = inject(Router);

  const currentUser = authenticationService.currentUserValue;

    if (currentUser?.token !== undefined) {  
      // check if route is restricted by role
     const isSuperAdmin = currentUser.roles?.includes(Role.SuperAdmin) ?? false;

      if (route.data['roles'] && route.data['roles'].length > 0 && !isSuperAdmin) {
        // role not authorized so redirect to home page
        // router.navigate([allRoutes.superAdminDashboard]);
        // return false;
        return router.createUrlTree([allRoutes.superAdminDashboard]);
      }
      // authorized so return true
      return true;
    }

    // not logged in so redirect to login page with the return url
    if(state.url.includes(allRoutes.superAdminDashboard))
    {
      // router.navigate([allRoutes.superAdminLogin]);
      // return false;
      return router.createUrlTree([allRoutes.superAdminLogin]);
    }
    else
    {
      // router.navigate([allRoutes.login], { queryParams: { returnUrl: state.url } });
      // return false;
       return router.createUrlTree(
    [allRoutes.login],
    {
      queryParams: {
        returnUrl: state.url
      }
    }
  );
    }
    // throw new Error('Method not implemented.');
    // return false;

}

// export const superAdminLoginGuard: CanActivateFn = () => {
//   const authService = inject(AuthenticationAuthorizationService);
//   const router = inject(Router);

//   const currentUser = authService.currentUserValue;

//   // if (currentUser && currentUser.token) {
//   //   // Already logged in! Instantly bounce them to the dashboard
//   //   router.navigate([allRoutes.superAdminDashboard]);
//   //   return false;
//   // }
//   // return true;
//   return currentUser?.token
//     ? router.createUrlTree([allRoutes.superAdminDashboard])
//     : true;
// };