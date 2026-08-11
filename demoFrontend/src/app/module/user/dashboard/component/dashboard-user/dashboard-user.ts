import { Component, inject, PLATFORM_ID, Renderer2 } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { constant } from '../../../../../utils/allRoutes/constant';
import { CommonFun } from '../../../../../utils/helper/CommonFun';
import { SideBar } from '../layout/side-bar/side-bar';
import { NavBar } from '../layout/nav-bar/nav-bar';
import { Footer } from '../layout/footer/footer';
import { isPlatformBrowser } from '@angular/common';


@Component({
  selector: 'app-dashboard-user',
  imports: [SideBar, NavBar, Footer, RouterOutlet],
  templateUrl: './dashboard-user.html',
  styleUrl: './dashboard-user.scss',
})
export class DashboardUser {

  pageTitle: string = ''; // "global" for this scope
    private platformId = inject(PLATFORM_ID);

  constructor(private commonFunctionObject: CommonFun, private renderer: Renderer2, private router: Router) {

  }

  ngOnInit(): void {

    console.log('----Dashboard-user module running--------ngOnInit------');

    if (isPlatformBrowser(this.platformId)) {
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
