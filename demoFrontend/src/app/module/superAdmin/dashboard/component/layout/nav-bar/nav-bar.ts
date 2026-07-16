import { Component, inject, Input, OnInit, PLATFORM_ID, Renderer2 } from '@angular/core';
import { AuthenticationService } from '../../../../../../service/authentication-service';
import { Router, RouterModule } from '@angular/router';
import { allRoutes } from '../../../../../../utils/allRoutes/allRoutes';
import { SuperAdminService } from '../../../../../../service/superAdmin/super-admin-service';
import { isPlatformBrowser } from '@angular/common';
import { CommonFun } from '../../../../../../utils/helper/CommonFun';

@Component({
  selector: 'app-nav-bar',
  imports: [RouterModule],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.scss',
})
export class NavBar implements OnInit {

  @Input() navBarPageTitle: string = '';
  displayEmail: string | undefined;
  profileImage: string = 'assets/superAdminModule/images/user/user-xs-01.jpg';
  isBrowser: boolean = false;
  private platformId = inject(PLATFORM_ID);

  constructor(private authenticationServiceObject: AuthenticationService, private router: Router, private SuperAdminServiceObject: SuperAdminService,private renderer: Renderer2,private commonFunctionObject: CommonFun,) {
    // this.commonFunctionObject.loadStyle( this.renderer, 'assets/superAdminModule/simplebar/simplebar.css' );
    //  this.commonFunctionObject.loadScript( this.renderer, 'assets/superAdminModule/simplebar/simplebar.min.js' );
    this.isBrowser = isPlatformBrowser(this.platformId);
       if (isPlatformBrowser(this.platformId))
       {
            this.displayEmail = this.authenticationServiceObject.currentUserValue.Subject;
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
    this.router.navigate([allRoutes.superAdminLogin]);
    // Implement your logout logic here
    // For example, you might want to clear user data and redirect to the login page
  }
}
