import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { allRoutes } from '../../../utils/allRoutes/allRoutes';
import { authGuard } from '../../../utils/guard/auth-guard';
import { Role } from '../../../service/authentication-service';

const routes: Routes = [
  {
    path: '', loadComponent: () => import('./component/dashboard-user/dashboard-user').then(m => m.DashboardUser),
    children: [
      { path: '', loadComponent: () => import('./component/dashboard-content/dashboard-content').then(m => m.DashboardContent) },
      { path: allRoutes.findFriend, loadComponent: () => import('./component/find-friend/find-friend').then(m => m.FindFriend) },
      { path: allRoutes.chatBox, loadComponent: () => import('./component/chat-box/chat-box').then(m => m.ChatBox) },
      // { path: allRoutes.manageRole, loadComponent: () => import('./component/manage-role/manage-role').then(m => m.ManageRole) },
      // { path: allRoutes.createPermission, loadComponent: () => import('./component/create-permission/create-permission').then(m => m.CreatePermission)},
      // { path: 'editPermission/:id', loadComponent: () => import('./component/create-permission/create-permission').then(m => m.CreatePermission)},
      // { path: allRoutes.managePermission, loadComponent: () => import('./component/manage-permission/manage-permission').then(m => m.ManagePermission) },
      { path: allRoutes.manageProfile, loadComponent: () => import('./../../superAdmin/dashboard/component/manage-profile/manage-profile').then(m => m.ManageProfile) },
      // { path: allRoutes.manageUser, loadComponent: () => import('./component/manage-user/manage-user').then(m => m.ManageUser) },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DashboardRoutingModule { }
