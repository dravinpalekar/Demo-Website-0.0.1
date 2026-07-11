import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardSuperAdmin } from './dashboard-super-admin';
import { authGuardGuard } from '../../../utils/guard/auth-guard-guard';
import { DashboardContent } from '../dashboard-content/dashboard-content';
import { CreateRole } from '../create-role/create-role';
import { ManageRole } from '../manage-role/manage-role';
import { CreatePermission } from '../create-permission/create-permission';
import { ManagePermission } from '../manage-permission/manage-permission';
import { MyProfile } from '../my-profile/my-profile';

const routes: Routes = [{
  path: '' , component: DashboardSuperAdmin,
  children: [
    { path: '', component: DashboardContent },
      { path: 'createRole', component: CreateRole },
      { path: 'manageRole', component: ManageRole },
      { path: 'createPermission', component:  CreatePermission},
      { path: 'managePermission', component: ManagePermission },
      { path: 'myProfile', component: MyProfile },
  ],
  // path: '', 
  // // canActivate: [authGuardGuard],
  // loadComponent: () => import('./dashboard-super-admin').then(m => m.DashboardSuperAdmin)
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DashboardSuperAdminRoutingModule {}
