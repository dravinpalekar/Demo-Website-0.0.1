import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { LoginModel } from '../../model/requestModel/LoginModel';
import PasswordMatcherValidation from '../../utils/formValidation/PasswordMatcherValidation';

@Component({
  selector: 'app-forgot-password',
  imports: [ReactiveFormsModule,MatButtonModule,RouterModule],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.scss'
})
export class ForgotPassword {
loginModelObject: LoginModel = new LoginModel("", "");

  loginForm!: FormGroup;
  submitted = false;

  constructor(private fb: FormBuilder){

  }


    ngOnInit(): void {
    console.log("----Login module running--------ngOnInit------");
    this.loginForm = this.fb.group({
      emailAddress: new FormControl('', [ Validators.required,Validators.minLength(4),Validators.email]),
    });
  }

  get f(): { [key: string]: AbstractControl } {
    return this.loginForm.controls;
  }

  onSubmit() {
    this.submitted = true;
    if (this.loginForm.invalid) { return; }

   
    alert('Login successful!');
 
    }

}
