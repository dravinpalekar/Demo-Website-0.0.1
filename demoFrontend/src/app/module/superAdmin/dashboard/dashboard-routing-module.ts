import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { 
    path: '' ,loadComponent: () => import('./component/dashboard-super-admin/dashboard-super-admin').then(m => m.DashboardSuperAdmin),
    children: [
      { path: '', loadComponent: () => import('./component/dashboard-content/dashboard-content').then(m => m.DashboardContent) },
      { path: 'createRole', loadComponent: () => import('./component/create-role/create-role').then(m => m.CreateRole) },
      { path: 'manageRole',loadComponent: () => import('./component/manage-role/manage-role').then(m => m.ManageRole) },
      { path: 'createPermission',loadComponent: () => import('./component/create-permission/create-permission').then(m => m.CreatePermission)},
      { path: 'editPermission/:id',loadComponent: () => import('./component/create-permission/create-permission').then(m => m.CreatePermission)},
      { path: 'managePermission',loadComponent: () => import('./component/manage-permission/manage-permission').then(m => m.ManagePermission) },
      { path: 'myProfile',loadComponent: () => import('./component/manage-profile/manage-profile').then(m => m.ManageProfile) },
    ]
 }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DashboardRoutingModule {}
