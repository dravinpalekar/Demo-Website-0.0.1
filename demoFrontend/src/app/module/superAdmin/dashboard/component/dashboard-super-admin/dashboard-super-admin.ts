import { Component, OnInit, Renderer2 } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { Footer } from '../layout/footer/footer';
import { SideBar } from '../layout/side-bar/side-bar';
import { NavBar } from '../layout/nav-bar/nav-bar';
import { CommonFun } from '../../../../../utils/helper/CommonFun';
import { constant } from '../../../../../utils/allRoutes/constant';

@Component({
  selector: 'app-dashboard-super-admin',
  imports: [SideBar, NavBar, Footer, RouterOutlet],
  templateUrl: './dashboard-super-admin.html',
  styleUrl: './dashboard-super-admin.scss',
})
export class DashboardSuperAdmin implements OnInit {

  pageTitle: string = ''; // "global" for this scope

  constructor(private commonFunctionObject: CommonFun, private renderer: Renderer2, private router: Router) {

  }

  ngOnInit(): void {

    console.log('----Dashboard-Super-Admin module running--------ngOnInit------');

    this.commonFunctionObject.loadStyle(this.renderer, 'assets/superAdminModule/css/material/css/materialdesignicons.min.css');
    this.commonFunctionObject.loadStyle(this.renderer, 'assets/superAdminModule/simplebar/simplebar.css');
    this.commonFunctionObject.loadStyleAndStoreStyleId(this.renderer, 'assets/superAdminModule/css/style.css', 'dashboard-super-admin-style');

    this.commonFunctionObject.loadScriptWithOnLoadCallback(this.renderer, 'assets/superAdminModule/js/jquery.min.js', () => {
      this.commonFunctionObject.loadScriptWithOnLoadCallback(this.renderer, 'assets/superAdminModule/js/mono.js');
    }
    );
    // this.commonFunctionObject.loadScript( this.renderer, 'assets/superAdminModule/simplebar/simplebar.min.js' );

    this.pageTitle = constant.routes[this.router.url];

  }

  updateTitle(newTitle: string) {
    this.pageTitle = newTitle;
  }

  ngOnDestroy(): void {
    // Remove all styles
    for (const id of this.commonFunctionObject.styleIds)
      this.commonFunctionObject.removeCssAndJsFileById(this.renderer, id);
    this.commonFunctionObject.styleIds.clear();
    // // Remove all scripts
    // for (const id of this.scriptIds) this.removeById(id);
    // this.scriptIds.clear();
  }
}
