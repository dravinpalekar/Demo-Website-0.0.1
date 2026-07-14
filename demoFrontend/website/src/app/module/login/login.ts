import { Component } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { LoginModel } from '../../model/requestModel/LoginModel';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule,MatButtonModule,RouterModule ],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {

loginModelObject: LoginModel = new LoginModel("", "");

  loginForm!: FormGroup;
  submitted = false;

  constructor(private fb: FormBuilder){

  }


    ngOnInit(): void {
    console.log("----Login module running--------ngOnInit------");
    this.loginForm = this.fb.group({
      userName: new FormControl('', [ Validators.required,Validators.minLength(4)]),
      password: new FormControl('', [ Validators.required,Validators.pattern(/^(?=[^A-Z]*[A-Z])(?=[^a-z]*[a-z])(?=\D*\d).{8,}$/)]),
    });
  }

  get f(): { [key: string]: AbstractControl } {
    return this.loginForm.controls;
  }

  onSubmit() {
    this.submitted = true;
    if (this.loginForm.invalid) { return; }

    console.log(this.loginForm.get('userName')?.errors);
    alert('Login successful!');
    console.log(this.loginForm.value);
    }
}
