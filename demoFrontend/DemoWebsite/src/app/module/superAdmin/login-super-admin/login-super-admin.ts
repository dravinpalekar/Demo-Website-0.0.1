import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { LoginModel } from '../../../model/requestModel/LoginModel';
import { allRoutes } from '../../../utils/allRoutes/allRoutes';
import { AuthenticationAuthorizationService } from '../../../service/authentication-authorization-service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Errors } from '../../../utils/helper/Errors';
import { CommonFunction } from '../../../utils/helper/CommonFunction';

@Component({
  selector: 'app-login-super-admin',
  imports: [ReactiveFormsModule, MatButtonModule, RouterModule, MatSnackBarModule],
  templateUrl: './login-super-admin.html',
  styleUrl: './login-super-admin.scss',
})
export class LoginSuperAdmin {

  loginForm!: FormGroup;
  submitted = false;

  constructor(
    private errorObject: Errors,  private commonFunctionObject: CommonFunction, private snackBarObject: MatSnackBar, private fb: FormBuilder, private authenticationAuthorizationServiceObject: AuthenticationAuthorizationService, private router: Router) {
    if (this.authenticationAuthorizationServiceObject.currentUserValue) {
      this.router.navigate([allRoutes.superAdminDashboard]);
    }
  }

  ngOnInit(): void {
    console.log("----Login-Super-Admin module running--------ngOnInit------");
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

    this.authenticationAuthorizationServiceObject.loginUser(loginModelObject).subscribe({
      next: (res) => {
        // console.log(res);
        res.roles.forEach((element: any) => {
          this.router.navigate([allRoutes.superAdminDashboard]);
        });
      },
      error: (e) => {
        //bad credentials
        if (e.status == 401) {
          this.commonFunctionObject.openSnackBar(e.error.detail,'danger');
        }
        else if (e.status == 406) {
          this.commonFunctionObject.openSnackBar(this.errorObject.errorStatus406(JSON.parse(JSON.stringify(e.error))),'danger');
        }
      }
    });
  }

}
