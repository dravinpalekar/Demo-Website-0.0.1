import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ForgotPassword } from './forgot-password';

const routes: Routes = [{ 
    path: '' , component: ForgotPassword,
    // children: [
    //   { path: '', component: Login },
    // ]
 }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ForgotPasswordRoutingModule { }
