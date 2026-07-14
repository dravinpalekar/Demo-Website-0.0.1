import { Component } from '@angular/core';
import {
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
  AbstractControl,
  ReactiveFormsModule,
} from '@angular/forms';
import PasswordMatcherValidation from '../../../utils/formValidation/PasswordMatcherValidation';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterModule } from '@angular/router';
import { AuthenticationService } from '../../../service/authentication-service';
import { Errors } from '../../../utils/helper/Errors';
import { signUpModel } from '../../../model/requestModel/signUpModel';
import { CommonFunction } from '../../../utils/helper/CommonFunction';

@Component({
  selector: 'app-sign-up-super-admin',
  imports: [ReactiveFormsModule, RouterModule, MatButtonModule],
  templateUrl: './sign-up-super-admin.html',
  styleUrl: './sign-up-super-admin.scss',
})

export class SignUpSuperAdmin {


  superAdminSignUpForm!: FormGroup;
  submittedForm = false;

  constructor(
    private errorObject: Errors,
    private formBuilderObject: FormBuilder,
    private AuthenticationServiceObject: AuthenticationService,
    private router: Router,
    private commonFunctionObject: CommonFunction
  ) { }

  ngOnInit(): void {
    console.log('----Sign-Up-Super-Admin module running--------ngOnInit------');
    this.superAdminSignUpForm = this.formBuilderObject.group(
      {
        emailAddress: new FormControl('', [Validators.required, Validators.minLength(4), Validators.email,]),
        password: new FormControl('', [Validators.required, Validators.pattern(/^(?=[^A-Z]*[A-Z])(?=[^a-z]*[a-z])(?=\D*\d).{8,}$/),]),
        confirmPassword: new FormControl('', [Validators.required]),
      },
      {
        validators: [
          PasswordMatcherValidation.match('password', 'confirmPassword'),
        ],
      }
    );
  }

  get f(): { [key: string]: AbstractControl } {
    return this.superAdminSignUpForm.controls;
  }

  onSubmit() {

    this.submittedForm = true;
    if (this.superAdminSignUpForm.invalid) {  return;  }

    const signUpModelObject: signUpModel = new signUpModel(this.superAdminSignUpForm.get('emailAddress')?.value, this.superAdminSignUpForm.get('password')?.value, ['superAdmin']);

    this.AuthenticationServiceObject.signUpUser(signUpModelObject).subscribe({
      next: (res) => {
        // console.log(res);
        this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message,'redNoMatch');
      },
      error: (e) => {
        // console.log(e);
        if (e.status == 400) {
          this.commonFunctionObject.openSnackBar(e.error.error, 'redNoMatch');
        } else if (e.status == 406) {
          this.commonFunctionObject.openSnackBar(this.errorObject.errorStatus406(JSON.parse(JSON.stringify(e.error))),'redNoMatch');
        }
      },
    });
  }
}
