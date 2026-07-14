import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginSuperAdmin } from './login-super-admin';


const routes: Routes = [{ 
    path: '' , loadComponent: () => import('./login-super-admin').then(m => m.LoginSuperAdmin) ,
    // children: [
    //   { path: '', component: Login },
    // ]
 }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LoginSuperAdminRoutingModule { }
