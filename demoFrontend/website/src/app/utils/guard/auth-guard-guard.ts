import { ActivatedRouteSnapshot, Route, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService, Role } from '../../service/authentication-service';
import { Injectable } from '@angular/core';
import { allRoutes } from '../allRoutes/allRoutes';

@Injectable({
  providedIn: 'root'
})
export class authGuardGuard{
constructor(private router: Router, private authenticationService: AuthenticationService) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {

    const currentUser = this.authenticationService.currentUserValue;
    if (currentUser?.token !== undefined) {  
      // check if route is restricted by role
     const isSuperAdmin = currentUser.roles?.includes(Role.SuperAdmin) ?? false;

      if (route.data['roles'] && route.data['roles'].length > 0 && !isSuperAdmin) {
        // role not authorized so redirect to home page
        this.router.navigate([allRoutes.superAdminLogin]);
        return false;
      }

      // authorized so return true
      return true;
    }

    // not logged in so redirect to login page with the return url
    if(state.url.includes(allRoutes.superAdminDashboard))
    {
      this.router.navigate([allRoutes.superAdminLogin]);
      return false;
    }
    else
    {
      this.router.navigate([allRoutes.login], { queryParams: { returnUrl: state.url } });
      return false;
    }
    // throw new Error('Method not implemented.');
  }

  // canLoad(route: Route, segments: UrlSegment[]): Observable<boolean> | Promise<boolean> | boolean {
  //   const currentUser = this.authenticationService.currentUserValue;

  //   if (currentUser?.token !== undefined) {
  //     const isSuperAdmin = currentUser.roles?.includes(Role.SuperAdmin) ?? false;

  //     if (route.data?.['roles'] && route.data['roles'].length > 0 && !isSuperAdmin) {
  //       // Optional: navigate manually (but avoid it in canLoad), or just return false
  //       this.router.navigate(['/superAdmin']);
  //       return false;
  //     }

  //     return true;
  //   }

  //   // not logged in so redirect to login page
  //   const url = '/' + segments.map(s => s.path).join('/');
  //   if (url.includes('adminDashboard')) {
  //     this.router.navigate(['/superAdmin'], { queryParams: { returnUrl: url } });
  //     return false;
  //   } else {
  //     this.router.navigate(['/login'], { queryParams: { returnUrl: url } });
  //     return false;
  //   }
  //   // throw new Error('Method not implemented.');
  // }
}
