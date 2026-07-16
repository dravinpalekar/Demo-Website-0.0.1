import { Routes } from '@angular/router';
import { allRoutes } from './utils/allRoutes/allRoutes';
import { Role } from './service/authentication-service';
import { authGuard } from './utils/guard/auth-guard';

export const routes: Routes = [
    {
        path: '',
        loadComponent: () => import('./component/landing-page/landing-page').then((m) => m.LandingPage),
    },
    {
        path: allRoutes.login,
        loadComponent: () => import('./component/login/login').then(m => m.Login)
    },
    {
        path: allRoutes.signUp,
        loadComponent: () => import('./component/sign-up/sign-up').then(m => m.SignUp)
    },
    {
        path: allRoutes.superAdminLogin,
        loadComponent: () => import('./component/superAdmin/login/login').then(m => m.Login)
    },
    {
        path: allRoutes.superAdminSignUp,
        loadComponent: () => import('./component/superAdmin/sign-up/sign-up').then(m => m.SignUp)
    },
    {
        path: allRoutes.superAdminDashboard,
        loadChildren: () => import('./module/superAdmin/dashboard/dashboard-module').then(m => m.DashboardModule),
        canActivate: [authGuard],
        data: { roles: [Role.SuperAdmin, Role.Admin] },
    },
    {
        path: allRoutes.notFound,
        loadComponent: () => import('./component/page-not-found/page-not-found').then(m => m.PageNotFound)
    },
    {
        path: '**',
        redirectTo: allRoutes.notFound,
        pathMatch: 'full',
    }
];
