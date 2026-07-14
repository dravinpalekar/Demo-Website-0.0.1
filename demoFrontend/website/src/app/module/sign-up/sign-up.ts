import { Component } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators, AbstractControl, ReactiveFormsModule } from '@angular/forms';
import { LoginModel } from '../../model/requestModel/LoginModel';
import { RouterModule } from '@angular/router';
import PasswordMatcherValidation from '../../utils/formValidation/PasswordMatcherValidation';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-sign-up',
  imports: [ReactiveFormsModule,RouterModule,MatButtonModule],
  templateUrl: './sign-up.html',
  styleUrl: './sign-up.scss'
})
export class SignUp {

  loginModelObject: LoginModel = new LoginModel("", "");

  signUpForm!: FormGroup;
  submitted = false;

  constructor(private fb: FormBuilder){

  }


    ngOnInit(): void {
    console.log("----SignUp module running--------ngOnInit------");
    this.signUpForm = this.fb.group({
      emailAddress: new FormControl('', [ Validators.required,Validators.minLength(4),Validators.email]),
      password: new FormControl('', [ Validators.required,Validators.pattern(/^(?=[^A-Z]*[A-Z])(?=[^a-z]*[a-z])(?=\D*\d).{8,}$/)]),
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
    this.submitted = true;
    if (this.signUpForm.invalid) { return; }

    // console.log(this.signUpForm.get('userName')?.errors);
    alert('SignUp successful!');
    // console.log(this.signUpForm.value);
    }

}
