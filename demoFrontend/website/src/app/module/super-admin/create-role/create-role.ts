import { ChangeDetectorRef, Component, ElementRef, Inject, Input, OnDestroy, OnInit, PLATFORM_ID, ViewChild } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AlertMessage } from "../layout/alert-message/alert-message";
import { SuperAdminService } from '../../../service/super-admin-service';
import { CommonFunction } from '../../../utils/helper/CommonFunction';
import { Errors } from '../../../utils/helper/Errors';
import { createRoleModel } from '../../../model/requestModel/superAdmin/createRoleModel';

@Component({
  selector: 'app-create-role',
  imports: [ReactiveFormsModule, AlertMessage],
  templateUrl: './create-role.html',
  styleUrl: './create-role.scss',
})
export class CreateRole implements OnInit, OnDestroy {

  @ViewChild('dropdown') dropdownElement!: ElementRef<HTMLSelectElement>;
  @ViewChild('dropdownTwo') dropdownTwoElement!: ElementRef<HTMLSelectElement>;

  @ViewChild('dropdownTwo') set dropdownTwoSetter(element: ElementRef<HTMLSelectElement> | undefined) {
    if (element && !this.choicesInstanceTwo && this.isBrowser) {
      this.dropdownTwoElement = element;
      this.commonFunctionObject.selectDropDownConfigWithChoicesJs(element,"Please select Permission Name","Type to search here...")
        .then(instance => this.choicesInstanceTwo = instance);
    }
  }

  @Input() closed: Boolean = false;

  createRoleForm!: FormGroup;
  submittedForm = false;
  showAlert = true;
  showSecondOption = false;
  displayAlertErrorList:string[] = [];
  private choicesInstance: any;
  private choicesInstanceTwo: any;
  private isBrowser: boolean;

  constructor(
    @Inject(PLATFORM_ID) platformId: object, private formBuilderObject: FormBuilder, private cdr: ChangeDetectorRef,
    private superAdminServiceObject: SuperAdminService, private commonFunctionObject: CommonFunction, private errorObject: Errors) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  ngOnInit() {

    console.log('----Create-role-Super-Admin module running--------ngOnInit------');
    this.createRoleForm = this.formBuilderObject.group({
      roleList: new FormControl('', [Validators.required]),
      permissionName: new FormControl('', [Validators.required]),
    });

    // Use valueChanges just for show/hide toggle
    this.createRoleForm.get('roleList')?.valueChanges.subscribe(value => {
      this.showSecondOption = !!value;

      // Reset permission when role changes
      if (!this.showSecondOption) {
        this.createRoleForm.get('permissionName')?.reset();
        if (this.choicesInstanceTwo) {
          this.choicesInstanceTwo.clearStore();
          this.choicesInstanceTwo = null; // force re-init next time
        }
      }
    });
  }


  onSubmit() {

    this.submittedForm = true;
    if (this.createRoleForm.invalid) {
      this.showAlert = true;
      this.calculateDisplayErrorsForAlertBox();
      return;
    }
    // Checked all input field validation
    const createRoleModelObject: createRoleModel = new createRoleModel(this.createRoleForm.get('roleList')?.value,this.createRoleForm.get('permissionName')?.value);

    this.superAdminServiceObject.createRole(createRoleModelObject).subscribe({
      next: (res) => {// console.log(res);
        this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'green');
      },
      error: (e) => {// console.log(e);
        if (e.status == 400) { this.commonFunctionObject.openSnackBar(e.error.detail, 'red'); }
        else 
        if (e.status == 422) { this.commonFunctionObject.openSnackBar(this.errorObject.directDisplayErrorMessageStatus406(JSON.parse(JSON.stringify(e.error))), 'red'); }
      },
    });

    // remove selected item from dropdown after submit
    if (this.choicesInstance) {
      this.createRoleForm.reset();
      this.choicesInstance.removeActiveItems();
      // this.choicesInstance.setChoiceByValue('');
      this.showAlert = false;
    }

  }

  async ngAfterViewInit() {
    if (this.isBrowser && this.dropdownElement) {// Initialize Role dropdown immediately
      this.choicesInstance = await this.commonFunctionObject.selectDropDownConfigWithChoicesJs(this.dropdownElement,"Please select Role Name","Type to search here...");
    }
  }

  get f(): { [key: string]: AbstractControl } { return this.createRoleForm.controls; }

    private calculateDisplayErrorsForAlertBox() {
    this.displayAlertErrorList = [];
    Object.keys(this.createRoleForm.controls).forEach( controlsName =>{
      const errors = this.f[controlsName]?.errors;
        if (errors) {
          switch (controlsName){
            case 'roleList':
            errors['required'] ? this.displayAlertErrorList.push('Role Name is required.') :null;
            break;
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
    if (this.isBrowser && this.choicesInstanceTwo) {
      this.choicesInstanceTwo.destroy();
    }
  }

}
