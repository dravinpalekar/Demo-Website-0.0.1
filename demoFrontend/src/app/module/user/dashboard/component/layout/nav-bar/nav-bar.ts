import { Component, inject, Input, PLATFORM_ID, Renderer2 } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthenticationService } from '../../../../../../service/authentication-service';
import { SuperAdminService } from '../../../../../../service/superAdmin/super-admin-service';
import { allRoutes } from '../../../../../../utils/allRoutes/allRoutes';
import { CommonFun } from '../../../../../../utils/helper/CommonFun';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-nav-bar',
  imports: [RouterModule],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.scss',
})
export class NavBar {

  @Input() navBarPageTitle: string = '';
  displayEmail: string | undefined;
  profileImage: string = 'assets/superAdminModule/images/user/user-xs-01.jpg';
  private cookieService = inject(CookieService);

  constructor(private authenticationServiceObject: AuthenticationService, private router: Router, private SuperAdminServiceObject: SuperAdminService, private renderer: Renderer2, private commonFunctionObject: CommonFun,) {
    // this.commonFunctionObject.loadStyle( this.renderer, 'assets/superAdminModule/simplebar/simplebar.css' );
    //  this.commonFunctionObject.loadScript( this.renderer, 'assets/superAdminModule/simplebar/simplebar.min.js' );
    if (this.cookieService.get("isLoggedIn")) {
      this.displayEmail = JSON.parse(this.cookieService.get("userSession")).userName;
    }
  }

  ngOnInit(): void {
    console.log("----nav-bar-Super-Admin module running--------ngOnInit------");

    this.SuperAdminServiceObject.getMyImage().subscribe({
      next: (res) => { //console.log(JSON.parse(JSON.stringify(res)).data);
        let responseData = JSON.parse(JSON.stringify(res));
        if (responseData.image == null || responseData.image == undefined || responseData.image == '')
          if (responseData.gender == "MALE")
            this.profileImage = 'assets/superAdminModule/images/user/default-image-men.jpg';
          else
            this.profileImage = 'assets/superAdminModule/images/user/default-image-women.jpg';
        else
          this.profileImage = responseData.image;
      },
      error: (e) => {// console.log(e);
      },
    });
  }

  logout() {

    this.authenticationServiceObject.logout();
    this.router.navigate([allRoutes.login]);
    // Implement your logout logic here
    // For example, you might want to clear user data and redirect to the login page
  }
}
