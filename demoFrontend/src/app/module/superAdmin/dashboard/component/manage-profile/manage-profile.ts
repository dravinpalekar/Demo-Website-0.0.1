import { isPlatformBrowser } from '@angular/common';
import { ChangeDetectorRef, Component, ElementRef, Inject, PLATFORM_ID, ViewChild } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormBuilder, FormControl, Validators, AbstractControl } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UpdateMyProfileModel } from '../../../../../model/requestModel/superAdmin/UpdateMyProfileModel';
import { AuthenticationService } from '../../../../../service/authentication-service';
import { SuperAdminService } from '../../../../../service/superAdmin/super-admin-service';
import { Errors } from '../../../../../utils/helper/Errors';
import { AlertMessage } from '../layout/alert-message/alert-message';
import { CommonFun } from '../../../../../utils/helper/CommonFun';
import { FileValidation } from '../../../../../utils/formValidation/FileValidation';

@Component({
  selector: 'app-manage-profile',
  imports: [ReactiveFormsModule, RouterModule, AlertMessage],
  templateUrl: './manage-profile.html',
  styleUrl: './manage-profile.scss',
})
export class ManageProfile {

  @ViewChild('dropdownGender') dropdownElementGender!: ElementRef<HTMLSelectElement>;
  @ViewChild('dropdownCountry') dropdownElementCountry!: ElementRef<HTMLSelectElement>;

  displayEmail?: string = "";
  showAlert = false;
  fullName = "";
  myProfileForm!: FormGroup;
  submittedForm = false;
  image!:any;
  private choicesInstanceGender: any;
  private choicesInstanceCountry: any;
  private isBrowser: boolean;
  displayAlertErrorList: string[] = [];

  file: File | null = null;
  preview: string | ArrayBuffer | null = null;
  showFileUploadOption = true;
  showFileUploadPreviewOption = false;

  constructor(private authenticationServiceObject: AuthenticationService, private fb: FormBuilder, @Inject(PLATFORM_ID) platformId: object, private commonFunctionObject: CommonFun, private SuperAdminServiceObject: SuperAdminService, private errorObject: Errors, private cd: ChangeDetectorRef) {
    this.isBrowser = isPlatformBrowser(platformId);
    if (isPlatformBrowser(platformId)) {
      this.displayEmail = this.authenticationServiceObject.currentUserValue.Subject;
    }
  }

  ngOnInit(): void {

    console.log('----my-profile-Super-Admin module running--------ngOnInit------');
    this.myProfileForm = this.fb.group({
      firstName: new FormControl('', [Validators.required, Validators.minLength(4)]),
      middleName: [''],
      lastName: new FormControl('', [Validators.required, Validators.minLength(3)]),
      gender: new FormControl('', [Validators.required]),
      age: new FormControl('', [Validators.required, Validators.min(4), Validators.max(100)]),
      country: new FormControl('', [Validators.required]),
      pinCode: new FormControl('', [Validators.required, Validators.minLength(6)]),
      city: new FormControl('', [Validators.required, Validators.minLength(3)]),
      address: new FormControl('', [Validators.required, Validators.minLength(10)]),
      uploadImage: new FormControl(null, [FileValidation(['image/jpeg', 'image/jpg', 'image/jpe', 'image/png'], 1)])
    });

    this.SuperAdminServiceObject.getMyImage().subscribe({
      next: (res) => {
        this.image = JSON.parse(JSON.stringify(res)).image;
      }
    })
  }

  async ngAfterViewInit() {
    if (this.isBrowser && this.dropdownElementGender && this.dropdownElementCountry) {// Initialize two dropdown immediately
      this.choicesInstanceGender = await this.commonFunctionObject.selectDropDownConfigWithChoicesJs(this.dropdownElementGender, "Please select Gender", "Type to search here...");
      this.choicesInstanceCountry = await this.commonFunctionObject.selectDropDownConfigWithChoicesJs(this.dropdownElementCountry, "Please select Country", "Type to search here...");

      this.SuperAdminServiceObject.getMyProfile().subscribe({
        next: (res) => { //console.log(JSON.parse(JSON.stringify(res)).data);
          let responseData = JSON.parse(JSON.stringify(res)).data;
          if (responseData) {
            this.myProfileForm.patchValue({
              firstName: responseData.firstName,
              middleName: responseData.middleName,
              lastName: responseData.lastName,
              gender: responseData.gender,
              age: responseData.age,
              country: responseData.country,
              pinCode: responseData.pinCode,
              city: responseData.city,
              address: responseData.address
            });
            this.fullName = responseData.firstName + " " + responseData.middleName + " " + responseData.lastName;
            this.choicesInstanceGender.setChoiceByValue(responseData.gender);
            this.choicesInstanceCountry.setChoiceByValue(responseData.country);
          }
        },
        error: (e) => {// console.log(e);
        },
      });
    }
  }

  get f(): { [key: string]: AbstractControl } { return this.myProfileForm.controls; }

  onSubmit() {
    this.submittedForm = true;
    if (this.myProfileForm.invalid) { this.showAlert = true; this.calculateDisplayErrorsForAlertBox(); return; }

    const updateMyProfileModelObject: UpdateMyProfileModel = new UpdateMyProfileModel(this.myProfileForm.get('firstName')?.value, this.myProfileForm.get('middleName')?.value, this.myProfileForm.get('lastName')?.value, this.myProfileForm.get('gender')?.value, this.myProfileForm.get('age')?.value, this.myProfileForm.get('country')?.value, this.myProfileForm.get('city')?.value, this.myProfileForm.get('pinCode')?.value, this.myProfileForm.get('address')?.value, this.file!);

    this.SuperAdminServiceObject.updateMyProfile(updateMyProfileModelObject).subscribe({
      next: (res) => {// console.log(res);
        this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');
      },
      error: (e) => {// console.log(e);
        if (e.status == 400) { this.commonFunctionObject.openSnackBar(e.error.detail, 'danger'); }
        else
          if (e.status == 422) { this.commonFunctionObject.openSnackBar(this.errorObject.directDisplayErrorMessageStatus406(JSON.parse(JSON.stringify(e.error))), 'danger'); }
      },
    });
    this.myProfileForm.get('uploadImage')?.reset();
    this.showFileUploadOption = true;
    this.showFileUploadPreviewOption = false;
    this.file = null;
  }

  onAlertClosed() {
    this.showAlert = false;
    // this.submittedForm = true;

  }

  ngOnDestroy() {
    if (this.isBrowser && this.choicesInstanceGender && this.choicesInstanceCountry) {
      this.choicesInstanceGender.destroy(); this.choicesInstanceCountry.destroy();
    }
  }

  private calculateDisplayErrorsForAlertBox() {
    this.displayAlertErrorList = [];
    Object.keys(this.myProfileForm.controls).forEach(controlsName => {
      const errors = this.f[controlsName]?.errors;
      if (errors) {
        switch (controlsName) {

          case 'firstName':
            errors['required'] && this.displayAlertErrorList.push('First name is required.');
            errors['minlength'] && this.displayAlertErrorList.push('Minimum length of first name is 4.');
            break;

          case 'lastName':
            errors['required'] && this.displayAlertErrorList.push('Last name is required.');
            errors['minlength'] && this.displayAlertErrorList.push('Minimum length of last name is 3.');
            break;

          case 'gender':
            this.displayAlertErrorList.push('Gender is required.');
            break;

          case 'age':
            errors['required'] && this.displayAlertErrorList.push('Age is required.');
            errors['min'] && this.displayAlertErrorList.push('Minimum age is 4.');
            errors['max'] && this.displayAlertErrorList.push('Maximum age allowed is 100.');
            break;

          case 'country':
            this.displayAlertErrorList.push('Country is required.');
            break;

          case 'city':
            errors['required'] && this.displayAlertErrorList.push('City is required.');
            errors['minlength'] && this.displayAlertErrorList.push('Minimum length of city is 3.');
            break;

          case 'pinCode':
            errors['required'] && this.displayAlertErrorList.push('Pin-Code is required.');
            errors['minlength'] && this.displayAlertErrorList.push('Minimum length of pin-code is 6.');
            break;

          case 'address':
            errors['required'] && this.displayAlertErrorList.push('Address is required.');
            errors['minlength'] && this.displayAlertErrorList.push('Minimum length of address is 10.');
            break;

          case 'uploadImage':
            errors['maxFileSize'] && this.displayAlertErrorList.push('File size too big. Please compress or choose a smaller file (Max: 1 MB).');
            errors['invalidFileType'] && this.displayAlertErrorList.push('This file type is not allowed. Please select a valid image file.(jpeg, jpg, jpe, png)');
            break;
        }
      }
    });
  }

  onFileDropped(event: DragEvent) {
    event.preventDefault();
    if (event.dataTransfer?.files?.length) {
      this.handleFile(event.dataTransfer.files[0]);
    }
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.handleFile(file);
    }
    event.target.value = "";    // clear input to allow re-selection of same file
  }

  previewFile() {
    this.showFileUploadOption = false;
    if (!this.file) return;
    const reader = new FileReader();
    reader.onload = () => {
      this.preview = reader.result;
      this.cd.detectChanges(); // ✅ ensures UI updates immediately
    };
    reader.readAsDataURL(this.file);
  }

  uploadFile() {
    if (!this.file) return;
    const formData = new FormData();
    formData.append('file', this.file);

    // TODO: Replace with your backend upload API
    console.log('Uploading:', this.file.name);
    // this.http.post('/api/upload', formData).subscribe(...)
  }

  private handleFile(file: File) {
    if (!file) return;

    this.file = file;
    this.myProfileForm.get('uploadImage')?.setValue(file);
    this.myProfileForm.get('uploadImage')?.updateValueAndValidity();
    this.myProfileForm.markAsDirty();

    this.showFileUploadPreviewOption = true;
    this.previewFile();
    // console.log(this.myProfileForm.get('uploadImage')?.errors);
  }
}
