import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators, AbstractControl, ReactiveFormsModule } from '@angular/forms';
import { LoginModel } from '../../model/requestModel/LoginModel';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import PasswordMatcherValidation from '../../utils/formValidation/PasswordMatcherValidation';
import { CommonFun } from '../../utils/helper/CommonFun';
import { signUpModel } from '../../model/requestModel/signUpModel';
import { AuthenticationService } from '../../service/authentication-service';

@Component({
  selector: 'app-sign-up',
  imports: [ReactiveFormsModule, RouterModule, MatButtonModule],
  templateUrl: './sign-up.html',
  styleUrl: './sign-up.scss',
})
export class SignUp implements OnInit {

  signUpForm!: FormGroup;
  submittedForm = false;

  constructor(private formBuilderObject: FormBuilder, private commonFunObject: CommonFun, private AuthenticationServiceObject: AuthenticationService) {

  }


  ngOnInit(): void {
    console.log("----SignUp component running--------ngOnInit------");
    this.signUpForm = this.formBuilderObject.group({
      emailAddress: new FormControl('', [Validators.required, Validators.minLength(4), Validators.email]),
      password: new FormControl('', [Validators.required, Validators.pattern(/^(?=[^A-Z]*[A-Z])(?=[^a-z]*[a-z])(?=\D*\d).{8,}$/)]),
      confirmPassword: new FormControl('', [Validators.required]),
    },
      {
        validators: [PasswordMatcherValidation.match('password', 'confirmPassword')]
      });
  }

  get f(): { [key: string]: AbstractControl } {
    return this.signUpForm.controls;
  }

  onSubmit() {
    this.submittedForm = true;
    if (this.signUpForm.invalid) { return; }

    const signUpModelObject: signUpModel = new signUpModel(this.signUpForm.get('emailAddress')?.value, this.signUpForm.get('password')?.value);

    this.AuthenticationServiceObject.signUpUser(signUpModelObject).subscribe({
      next: (res) => {
        // console.log(res);
        this.commonFunObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');
      },
      error: (e) => {
        // console.log(e);
        if (e.status == 400) {
          this.commonFunObject.openSnackBar(e.error.error, 'danger');
        } else if (e.status == 422) {
          this.commonFunObject.openSnackBar(e.error.error, 'danger');
        }
        else if (e.status == 405) {
          this.commonFunObject.openSnackBar(e.error.message, 'danger');
        }
      },
    });
  }
}
