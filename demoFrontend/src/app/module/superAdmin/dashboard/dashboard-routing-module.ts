import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { allRoutes } from '../../../utils/allRoutes/allRoutes';

const routes: Routes = [
  { 
    path: '' ,loadComponent: () => import('./component/dashboard-super-admin/dashboard-super-admin').then(m => m.DashboardSuperAdmin),
    children: [
      { path: '', loadComponent: () => import('./component/dashboard-content/dashboard-content').then(m => m.DashboardContent) },
      { path: allRoutes.createRole, loadComponent: () => import('./component/create-role/create-role').then(m => m.CreateRole) },
      { path: 'editRole/:id', loadComponent: () => import('./component/create-role/create-role').then(m => m.CreateRole) },
      { path: allRoutes.manageRole, loadComponent: () => import('./component/manage-role/manage-role').then(m => m.ManageRole) },
      { path: allRoutes.createPermission, loadComponent: () => import('./component/create-permission/create-permission').then(m => m.CreatePermission)},
      { path: 'editPermission/:id', loadComponent: () => import('./component/create-permission/create-permission').then(m => m.CreatePermission)},
      { path: allRoutes.managePermission, loadComponent: () => import('./component/manage-permission/manage-permission').then(m => m.ManagePermission) },
      { path: allRoutes.manageProfile, loadComponent: () => import('./component/manage-profile/manage-profile').then(m => m.ManageProfile) },
      { path: allRoutes.manageUser, loadComponent: () => import('./component/manage-user/manage-user').then(m => m.ManageUser) },
    ]
 }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DashboardRoutingModule {}
