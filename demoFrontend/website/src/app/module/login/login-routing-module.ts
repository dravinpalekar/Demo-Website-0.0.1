import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Login } from './login';


const routes: Routes = [{ 
    path: '' , loadComponent: () => import('./login').then(m => m.Login) ,
    // children: [
    //   { path: '', component: Login },
    // ]
 }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LoginRoutingModule { }
