import { Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { LoginModel } from '../../../model/requestModel/LoginModel';
import { allRoutes } from '../../../utils/allRoutes/allRoutes';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { Errors } from '../../../utils/helper/Errors';
import { AuthenticationService } from '../../../service/authentication-service';
import { CommonFun } from '../../../utils/helper/CommonFun';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, MatButtonModule, RouterModule, MatSnackBarModule,],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login implements OnInit {

  private cookieService = inject(CookieService);
  loginForm!: FormGroup;
  submitted = false;

  constructor(private errorObject: Errors, private fb: FormBuilder, private authServiceServiceObject: AuthenticationService, private router: Router, private commonFunObject: CommonFun, private route: ActivatedRoute,) {

    if(this.cookieService.get("isLoggedIn"))
    {
      this.router.navigate([this.route.snapshot.queryParams['returnUrl'] || allRoutes.superAdminDashboard]);
    }

  }


  ngOnInit(): void {
    console.log("----Login-Super-Admin component running--------ngOnInit------");
    this.loginForm = this.fb.group({
      userName: new FormControl('', [Validators.required, Validators.minLength(4)]),
      password: new FormControl('', [Validators.required, Validators.pattern(/^(?=[^A-Z]*[A-Z])(?=[^a-z]*[a-z])(?=\D*\d).{8,}$/)]),
    });
  }

  get f(): { [key: string]: AbstractControl } {
    return this.loginForm.controls;
  }

  onSubmit() {
    this.submitted = true;
    if (this.loginForm.invalid) { return; }

    const loginModelObject: LoginModel = new LoginModel(this.loginForm.get('userName')?.value, this.loginForm.get('password')?.value);

    this.authServiceServiceObject.loginUser(loginModelObject).subscribe({
      next: (res) => {

        // res.roles.forEach((element: any) => {
        this.router.navigate([this.route.snapshot.queryParams['returnUrl'] || allRoutes.superAdminDashboard]);
        // });
      },
      error: (e) => {
        //bad credentials
        if (e.status == 401) {
          this.commonFunObject.openSnackBar(e.error.detail, 'danger');
        }
        else if (e.status == 406) {
          this.commonFunObject.openSnackBar(this.errorObject.errorStatus406(JSON.parse(JSON.stringify(e.error))), 'danger');
        }
      }
    });
  }

}
