import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SignUpSuperAdmin } from './sign-up-super-admin';

const routes: Routes = [{ 
    path: '' , loadComponent: () => import('./sign-up-super-admin').then(m => m.SignUpSuperAdmin) ,
 }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SignUpSuperAdminRoutingModule { }
