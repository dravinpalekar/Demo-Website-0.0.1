import { Component, Renderer2 } from '@angular/core';
import { CommonFunction } from '../../../utils/helper/CommonFunction';
import { Router, RouterOutlet } from '@angular/router';
import { Footer } from '../layout/footer/footer';
import { Navbar } from '../layout/navbar/navbar';
import { SideBarMenu } from '../layout/side-bar-menu/side-bar-menu';
import { AuthenticationAuthorizationService } from '../../../service/authentication-authorization-service';
import { allRoutes } from '../../../utils/allRoutes/allRoutes';

@Component({
  selector: 'app-dashboard-super-admin',
  imports: [SideBarMenu, Navbar, Footer, RouterOutlet],
  templateUrl: './dashboard-super-admin.html',
  styleUrl: './dashboard-super-admin.scss',
})
export class DashboardSuperAdmin {

  pageTitle: string = ''; // "global" for this scope

  constructor(private commonFunctionObject: CommonFunction, private renderer: Renderer2, private router: Router, private authenticationAuthorizationServiceObject: AuthenticationAuthorizationService) { 
    
  }

  ngOnInit(): void {
    console.log("----Dashboard Super module running--------ngOnInit------");

    this.commonFunctionObject.loadStyle(this.renderer, 'assets/superAdminModule/css/material/css/materialdesignicons.min.css');
    this.commonFunctionObject.loadStyle(this.renderer, 'assets/superAdminModule/simplebar/simplebar.css');
    this.commonFunctionObject.loadStyleAndStoreStyleId(this.renderer, 'assets/superAdminModule/css/style.css', 'dashboard-super-admin-style');

    this.commonFunctionObject.loadScriptWithOnLoadCallback(this.renderer, 'assets/superAdminModule/js/jquery.min.js', () => {
      this.commonFunctionObject.loadScriptWithOnLoadCallback(this.renderer, 'assets/superAdminModule/js/mono.js');
    }
    );
    this.commonFunctionObject.loadScript(this.renderer, 'assets/superAdminModule/simplebar/simplebar.min.js');
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
