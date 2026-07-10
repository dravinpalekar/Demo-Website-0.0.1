import { Routes } from '@angular/router';
import { allRoutes } from './utils/allRoutes/allRoutes';
import { Role } from './service/authentication-authorization-service';
import { authGuardGuard } from './utils/guard/auth-guard-guard';
import { PageNotFound } from './module/superAdmin/layout/page-not-found/page-not-found';

export const routes: Routes = [

   {
      path: '',
      loadChildren: () => import('./module/landing-page/landing-page-module').then((m) => m.LandingPageModule),
   },

   {
      path: allRoutes.superAdminLogin,
      loadChildren: () => import('./module/superAdmin/login-super-admin/login-super-admin-module').then((m) => m.LoginSuperAdminModule),
   },

   {
      path: allRoutes.superAdminDashboard,
      loadChildren: () => import('./module/superAdmin/dashboard-super-admin/dashboard-super-admin-module').then(m => m.DashboardSuperAdminModule),
      canActivate: [authGuardGuard],
      //  canLoad: [ authGuardGuard ],
      data: { roles: [Role.SuperAdmin, Role.Admin] },
   },

   { path: 'notFound', component: PageNotFound },
   { path: '**', redirectTo: 'notFound', pathMatch: 'full', }
];
