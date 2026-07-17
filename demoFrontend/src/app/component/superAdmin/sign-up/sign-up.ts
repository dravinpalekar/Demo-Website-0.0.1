import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators, AbstractControl, ReactiveFormsModule } from '@angular/forms';
import { signUpModel } from '../../../model/requestModel/signUpModel';
import { AuthenticationService } from '../../../service/authentication-service';
import PasswordMatcherValidation from '../../../utils/formValidation/PasswordMatcherValidation';
import { Errors } from '../../../utils/helper/Errors';
import { CommonFun } from '../../../utils/helper/CommonFun';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-sign-up',
  imports: [ReactiveFormsModule, RouterModule, MatButtonModule],
  templateUrl: './sign-up.html',
  styleUrl: './sign-up.scss',
})
export class SignUp implements OnInit{

  
  superAdminSignUpForm!: FormGroup;
  submittedForm = false;

  constructor(
    private errorObject: Errors,
    private formBuilderObject: FormBuilder,
    private AuthenticationServiceObject: AuthenticationService,
    private router: Router,
    private commonFunObject: CommonFun
  ) { }

  ngOnInit(): void {
    console.log('----Sign-Up-Super-Admin component running--------ngOnInit------');
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
        this.commonFunObject.openSnackBar(JSON.parse(JSON.stringify(res)).message,'success');
      },
      error: (e) => {
        // console.log(e);
        if (e.status == 400) {
          this.commonFunObject.openSnackBar(e.error.error, 'danger');
        } else if (e.status == 422) {
          this.commonFunObject.openSnackBar(e.error.error,'danger');
        }
        else if (e.status == 405) {
          this.commonFunObject.openSnackBar(e.error.message,'danger');
        }
      },
    });
  }
}
