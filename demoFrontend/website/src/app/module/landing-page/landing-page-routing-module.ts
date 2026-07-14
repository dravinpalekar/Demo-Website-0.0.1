import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LandingPage } from './landing-page';

const routes: Routes = [
  { 
    path: '', loadComponent: () => import('./landing-page').then(m => m.LandingPage) 
    // children: [
    //   { path: '', loadComponent: () => import('./landing-page').then(m => m.LandingPage) },
    //   // { path: 'lay', component: Layout },
    // ]
 }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LandingPageRoutingModule { }
