import { ChangeDetectorRef, Component, inject, Input, PLATFORM_ID, Renderer2, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthenticationService } from '../../../../../../service/authentication-service';
import { SuperAdminService } from '../../../../../../service/superAdmin/super-admin-service';
import { allRoutes } from '../../../../../../utils/allRoutes/allRoutes';
import { CommonFun } from '../../../../../../utils/helper/CommonFun';
import { CookieService } from 'ngx-cookie-service';
import { isPlatformBrowser, NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'app-nav-bar',
  imports: [RouterModule, NgOptimizedImage],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.scss',
})
export class NavBar {

  @Input() navBarPageTitle: string = '';
  displayEmail: string | undefined;
  // profileImage: any;
  private cookieService = inject(CookieService);
  private platformId = inject(PLATFORM_ID);
  profileImage = signal<string>('');

  constructor(private authenticationServiceObject: AuthenticationService, private router: Router, private SuperAdminServiceObject: SuperAdminService, private renderer: Renderer2, private commonFunctionObject: CommonFun, private cdr: ChangeDetectorRef) {
    // this.commonFunctionObject.loadStyle( this.renderer, 'assets/superAdminModule/simplebar/simplebar.css' );
    //  this.commonFunctionObject.loadScript( this.renderer, 'assets/superAdminModule/simplebar/simplebar.min.js' );
    if (this.cookieService.get("isLoggedIn")) {
      this.displayEmail = JSON.parse(this.cookieService.get("userSession")).userName;
    }
  }

  ngOnInit(): void {
    console.log("----nav-bar-Super-Admin module running--------ngOnInit------");

    if (isPlatformBrowser(this.platformId)) {
      this.SuperAdminServiceObject.getMyImage().subscribe({
        next: (res) => { //console.log(JSON.parse(JSON.stringify(res)).data);
          let responseData = JSON.parse(JSON.stringify(res));
          // if (responseData.image == null || responseData.image == undefined || responseData.image == '')
          //   if (responseData.gender == "MALE")
          //     this.profileImage = 'assets/superAdminModule/images/user/default-image-men.jpg';
          //   else
          //     this.profileImage = 'assets/superAdminModule/images/user/default-image-women.jpg';
          // else
            this.profileImage.set( responseData.image);
            // this.profileImage = responseData.image;
          
          this.cdr.detectChanges();

        },
        error: (e) => {// console.log(e);
        },
      });
    }
  }

  logout() {

    this.authenticationServiceObject.logout();
    this.router.navigate([allRoutes.login]);
    // Implement your logout logic here
    // For example, you might want to clear user data and redirect to the login page
  }
}
