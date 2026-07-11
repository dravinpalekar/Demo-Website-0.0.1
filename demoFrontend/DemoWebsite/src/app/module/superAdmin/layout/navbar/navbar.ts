import { Component, Input } from '@angular/core';
import { AuthenticationAuthorizationService } from '../../../../service/authentication-authorization-service';
import { allRoutes } from '../../../../utils/allRoutes/allRoutes';
import { Router, RouterModule } from '@angular/router';
import { SuperAdminServices } from '../../../../service/superAdmin/super-admin-services';

@Component({
  selector: 'app-navbar',
  imports: [RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {

  @Input() navBarPageTitle: string = '';
  displayEmail: string | undefined;
  profileImage: string = 'assets/superAdminModule/images/user/user-xs-01.jpg';


  constructor(private authenticationAuthorizationService: AuthenticationAuthorizationService, private router: Router, private SuperAdminServiceObject: SuperAdminServices) {
    this.displayEmail = this.authenticationAuthorizationService.currentUserValue.Subject;
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

    this.authenticationAuthorizationService.logout();
    this.router.navigate([allRoutes.superAdminLogin]);
    // Implement your logout logic here
    // For example, you might want to clear user data and redirect to the login page
  }
}
