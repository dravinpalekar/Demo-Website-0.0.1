import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardSuperAdmin } from './dashboard-super-admin';
import { DashboardContent } from '../dashboard-content/dashboard-content';
import { CreateRole } from '../create-role/create-role';
import { ManageRole } from '../manage-role/manage-role';
import { CreatePermission } from '../create-permission/create-permission';
import { ManagePermission } from '../manage-permission/manage-permission';
import { MyProfile } from '../my-profile/my-profile';

const routes: Routes = [{ 
    path: '' ,loadComponent: () => import('./dashboard-super-admin').then(m => m.DashboardSuperAdmin),
    children: [
      { path: '', loadComponent: () => import('./../dashboard-content/dashboard-content').then(m => m.DashboardContent) },
      { path: 'createRole', loadComponent: () => import('./../create-role/create-role').then(m => m.CreateRole) },
      { path: 'manageRole',loadComponent: () => import('./../manage-role/manage-role').then(m => m.ManageRole) },
      { path: 'createPermission',loadComponent: () => import('./../create-permission/create-permission').then(m => m.CreatePermission)},
      { path: 'managePermission',loadComponent: () => import('./../manage-permission/manage-permission').then(m => m.ManagePermission) },
      { path: 'myProfile',loadComponent: () => import('./../my-profile/my-profile').then(m => m.MyProfile) },
    ]
 }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardSuperAdminRoutingModule { }
