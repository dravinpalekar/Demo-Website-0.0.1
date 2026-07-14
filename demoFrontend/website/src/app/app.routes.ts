import { Routes } from '@angular/router';
import { authGuardGuard } from './utils/guard/auth-guard-guard';
import { Role } from './service/authentication-service';
import { allRoutes } from './utils/allRoutes/allRoutes';
import { PageNotFound } from './module/super-admin/layout/page-not-found/page-not-found';

export const routes: Routes = [
   {
      path: '',
      loadChildren: () => import('./module/landing-page/landing-page-module').then((m) => m.LandingPageModule),
   },
   {
      path: allRoutes.login,
      loadChildren: () => import('./module/login/login-module').then((m) => m.LoginModule),
   },
   {
      path: allRoutes.signUp,
      loadChildren: () => import('./module/sign-up/sign-up-module').then((m) => m.SignUpModule),
   },
   {
      path: allRoutes.forgotPassword,
      loadChildren: () => import('./module/forgot-password/forgot-password-module').then((m) => m.ForgotPasswordModule),
   },
   {
      path: allRoutes.superAdminLogin,
      loadChildren: () => import('./module/super-admin/login-super-admin/login-super-admin-module').then((m) => m.LoginSuperAdminModule),
   },
   {
      path: allRoutes.superAdminSignUp,
      loadChildren: () => import('./module/super-admin/sign-up-super-admin/sign-up-super-admin-module').then((m) => m.SignUpSuperAdminModule),
   },
   {
      path: allRoutes.superAdminDashboard,
      loadChildren: () => import('./module/super-admin/dashboard-super-admin/dashboard-super-admin-module').then(m => m.DashboardSuperAdminModule),
      canActivate: [authGuardGuard],
      //  canLoad: [ authGuardGuard ],
      data: { roles: [Role.SuperAdmin, Role.Admin] },
   },
   { path: 'notFound', loadComponent: () => import('./module/super-admin/layout/page-not-found/page-not-found').then(m => m.PageNotFound)  },
   { path: '**', redirectTo: 'notFound', pathMatch: 'full', }
];
