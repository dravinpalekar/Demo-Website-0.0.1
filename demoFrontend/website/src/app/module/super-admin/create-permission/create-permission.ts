import { isPlatformBrowser } from '@angular/common';
import { Component, ElementRef, Inject, Input, PLATFORM_ID, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators, AbstractControl, ReactiveFormsModule } from '@angular/forms';
import { SuperAdminService } from '../../../service/super-admin-service';
import { CommonFunction } from '../../../utils/helper/CommonFunction';
import { Errors } from '../../../utils/helper/Errors';
import { AlertMessage } from '../layout/alert-message/alert-message';
import { createNameModel } from '../../../model/requestModel/superAdmin/createNameModel';

@Component({
  selector: 'app-create-permission',
  imports: [ReactiveFormsModule, AlertMessage],
  templateUrl: './create-permission.html',
  styleUrl: './create-permission.scss'
})
export class CreatePermission {


  @ViewChild('dropdown') dropdownElement!: ElementRef<HTMLSelectElement>;
  @Input() closed: Boolean = false;

  createPermissionForm!: FormGroup;
  submittedForm = false;
  showAlert = true;
  private choicesInstance: any;
  private isBrowser: boolean;
  displayAlertErrorList:string[] = [];

  constructor(
    @Inject(PLATFORM_ID) platformId: object, private formBuilderObject: FormBuilder,
    private superAdminServiceObject: SuperAdminService, private commonFunctionObject: CommonFunction,  private errorObject: Errors) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  ngOnInit() {

    console.log('----Create-permission-Super-Admin module running--------ngOnInit------');
    this.createPermissionForm = this.formBuilderObject.group({
        permissionName: new FormControl('', [Validators.required]),
      });
  }


  onSubmit() {

    this.submittedForm = true;
    if (this.createPermissionForm.invalid) {
      this.showAlert = true;
      this.calculateDisplayErrorsForAlertBox();
      return;
    }

    const createPermissionModelObject: createNameModel = new createNameModel(this.createPermissionForm.get('permissionName')?.value);

    this.superAdminServiceObject.createPermission(createPermissionModelObject).subscribe({
      next: (res) => {// console.log(res);
        this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'green');
      },
      error: (e) => {// console.log(e);
        if (e.status == 400) {
          this.commonFunctionObject.openSnackBar(e.error.detail, 'red');
        } else if (e.status == 406) {
          this.commonFunctionObject.openSnackBar(this.errorObject.errorStatus406(JSON.parse(JSON.stringify(e.error))), 'red');
        }
      },
    });

    // remove selected item from dropdown
    if (this.choicesInstance) {
        this.createPermissionForm.reset();
        this.choicesInstance.removeActiveItems();
        // this.choicesInstance.setChoiceByValue('');
         this.showAlert = false;
      }
  }


  async ngAfterViewInit() {
    if (this.isBrowser) {
     this.choicesInstance = await this.commonFunctionObject.selectDropDownConfigWithChoicesJs(this.dropdownElement, "Please select Permission Name", "Type to search here...");
    }
  }

  get f(): { [key: string]: AbstractControl } { return this.createPermissionForm.controls; }

  private calculateDisplayErrorsForAlertBox() {
    this.displayAlertErrorList = [];
    Object.keys(this.createPermissionForm.controls).forEach( controlsName =>{
      const errors = this.f[controlsName]?.errors;
        if (errors) {
          switch (controlsName){
            case 'permissionName':
            errors['required'] ? this.displayAlertErrorList.push('Permission Name is required.') :null;
            break;
          }
        }
    });
  }

  onAlertClosed() {
    this.showAlert = false;
  }

  ngOnDestroy() {
    if (this.isBrowser && this.choicesInstance) {
      this.choicesInstance.destroy();
    }
  }

}
