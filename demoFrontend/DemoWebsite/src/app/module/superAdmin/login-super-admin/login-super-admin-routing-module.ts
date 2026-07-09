import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginSuperAdminModule } from './login-super-admin-module';

const routes: Routes = [{
  path: '', 
  loadComponent: () => import('./login-super-admin').then(m => m.LoginSuperAdmin)
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LoginSuperAdminRoutingModule {}
