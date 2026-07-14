import { Component } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators, AbstractControl, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { LoginModel } from '../../../model/requestModel/LoginModel';
import { AuthenticationService } from '../../../service/authentication-service';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Snackbar } from '../../../utils/snackbar/snackbar';
import { Errors } from '../../../utils/helper/Errors';
import { allRoutes } from '../../../utils/allRoutes/allRoutes';

@Component({
  selector: 'app-login-super-admin',
  imports: [ReactiveFormsModule, MatButtonModule, RouterModule, MatSnackBarModule,],
  templateUrl: './login-super-admin.html',
  styleUrl: './login-super-admin.scss'
})
export class LoginSuperAdmin {


  loginForm!: FormGroup;
  submitted = false;

  constructor(private errorObject: Errors, private snackBarObject: MatSnackBar, private fb: FormBuilder, private authServiceServiceObject: AuthenticationService, private router: Router) {
    if (this.authServiceServiceObject.currentUserValue) {
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

    this.authServiceServiceObject.loginUser(loginModelObject).subscribe({
      next: (res) => {
        console.log(res);
        res.roles.forEach((element: any) => {
          this.router.navigate([allRoutes.superAdminDashboard]);
        });
      },
      error: (e) => {
        //bad credentials
        if (e.status == 401) {
          this.openSnackBar(e.error.detail);
        }
        else if (e.status == 406) {
          this.openSnackBar(this.errorObject.errorStatus406(JSON.parse(JSON.stringify(e.error))));
        }
      }
    });
  }

  public openSnackBar(data: any) {
    this.snackBarObject.openFromComponent(Snackbar, {
      data: data,
      panelClass: ['redNoMatch'],
      horizontalPosition: 'right',
      verticalPosition: 'top',
      duration: 1000 * 4
    });
  }


}
