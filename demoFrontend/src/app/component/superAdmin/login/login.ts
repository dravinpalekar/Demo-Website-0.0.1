import { Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { LoginModel } from '../../../model/requestModel/LoginModel';
import { allRoutes } from '../../../utils/allRoutes/allRoutes';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Errors } from '../../../utils/helper/Errors';
import { AuthenticationService } from '../../../service/authentication-service';
import { Snackbar } from '../../../utils/snackbar/snackbar';
import { isPlatformBrowser } from '@angular/common';
import { CommonFun } from '../../../utils/helper/CommonFun';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, MatButtonModule, RouterModule, MatSnackBarModule,],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login implements OnInit {


  loginForm!: FormGroup;
  submitted = false;
  isBrowser: boolean = false;
  private platformId = inject(PLATFORM_ID);
  constructor(private errorObject: Errors, private snackBarObject: MatSnackBar, private fb: FormBuilder, private authServiceServiceObject: AuthenticationService, private router: Router, private commonFunObject: CommonFun, private route: ActivatedRoute,) {

    this.isBrowser = isPlatformBrowser(this.platformId);
    if (isPlatformBrowser(this.platformId)) {
      if (this.authServiceServiceObject.currentUserValue?.token != undefined) {
        this.router.navigate([this.route.snapshot.queryParams['returnUrl'] || allRoutes.superAdminDashboard]);
      }
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
        console.log(res);
        res.roles.forEach((element: any) => {
          this.router.navigate([this.route.snapshot.queryParams['returnUrl'] || allRoutes.superAdminDashboard]);
        });
      },
      error: (e) => {
        //bad credentials
        if (e.status == 401) {
          // this.openSnackBar(e.error.detail);
          this.commonFunObject.openSnackBar(e.error.detail, 'danger');
        }
        else if (e.status == 406) {
          // this.openSnackBar(this.errorObject.errorStatus406(JSON.parse(JSON.stringify(e.error))));
          this.commonFunObject.openSnackBar(this.errorObject.errorStatus406(JSON.parse(JSON.stringify(e.error))), 'danger');
        }
      }
    });
  }

}
