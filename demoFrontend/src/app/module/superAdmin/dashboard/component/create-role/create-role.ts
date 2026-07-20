import { isPlatformBrowser } from '@angular/common';
import { ChangeDetectorRef, Component, ElementRef, EventEmitter, Inject, Input, OnDestroy, OnInit, Output, PLATFORM_ID, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators, AbstractControl, ReactiveFormsModule } from '@angular/forms';
import { createRoleModel } from '../../../../../model/requestModel/superAdmin/createRoleModel';
import { SuperAdminService } from '../../../../../service/superAdmin/super-admin-service';
import { Errors } from '../../../../../utils/helper/Errors';
import { AlertMessage } from '../layout/alert-message/alert-message';
import { CommonFun } from '../../../../../utils/helper/CommonFun';
import { ActivatedRoute, Router } from '@angular/router';
import { allRoutes } from '../../../../../utils/allRoutes/allRoutes';

@Component({
  selector: 'app-create-role',
  imports: [ReactiveFormsModule, AlertMessage],
  templateUrl: './create-role.html',
  styleUrl: './create-role.scss',
})
export class CreateRole implements OnInit, OnDestroy {

  @ViewChild('dropdown') dropdownElement!: ElementRef<HTMLSelectElement>;
  @ViewChild('dropdownTwo') dropdownTwoElement!: ElementRef<HTMLSelectElement>;

  // Temporary variable to hold the fetched permission name during edit mode
  private pendingPermissionName: string | null = null;

  @ViewChild('dropdownTwo') set dropdownTwoSetter(element: ElementRef<HTMLSelectElement> | undefined) {
    if (element && !this.choicesInstanceTwo && this.isBrowser) {
      this.dropdownTwoElement = element;
      this.commonFunctionObject.selectDropDownConfigWithChoicesJs(element, "Please select Permission Name", "Type to search here...").then(instance => {
        this.choicesInstanceTwo = instance;

        element.nativeElement.addEventListener('change', (event: any) => {
          this.createRoleForm.get('permissionName')?.setValue(event.target.value);
          this.createRoleForm.get('permissionName')?.updateValueAndValidity();
        });

        // CRITICAL FIX: If we have a pending value from Edit mode, set it now
        if (this.pendingPermissionName && this.choicesInstanceTwo) {
          this.choicesInstanceTwo.setChoiceByValue(this.pendingPermissionName);
          // 2. CRITICAL FIX: Update the actual Angular form validation engine!
          this.createRoleForm.get('permissionName')?.setValue(this.pendingPermissionName, { emitEvent: false });
          this.createRoleForm.get('permissionName')?.updateValueAndValidity();

          this.pendingPermissionName = null;
        }
      });
    }
  }

  @Input() closed: Boolean = false;
  @Output() pageTitle = new EventEmitter<string>();
  createRoleForm!: FormGroup;
  submittedForm = false;
  showAlert = false;
  showSecondOption = false;
  displayAlertErrorList: string[] = [];
  private choicesInstance: any;
  private choicesInstanceTwo: any;
  private isBrowser: boolean;
  isEditMode: boolean = false;
  editModeId: number | undefined;

  constructor(
    @Inject(PLATFORM_ID) platformId: object, private formBuilderObject: FormBuilder, private cdr: ChangeDetectorRef, private route: ActivatedRoute, private router: Router, private superAdminServiceObject: SuperAdminService, private commonFunctionObject: CommonFun, private errorObject: Errors) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  ngOnInit() {

    console.log('----Create-role-Super-Admin component running--------ngOnInit------');
    this.createRoleForm = this.formBuilderObject.group({
      roleList: new FormControl('', [Validators.required]),
      permissionName: new FormControl('', [Validators.required]),
    });

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.pageTitle.emit("Edit Role");
        this.isEditMode = true;
        this.editModeId = Number(id);
      }
    });

    // Use valueChanges just for show/hide toggle
    this.createRoleForm.get('roleList')?.valueChanges.subscribe(value => {
      this.showSecondOption = value;

      // Reset permission when role changes
      if (!this.showSecondOption) {
        this.createRoleForm.get('permissionName')?.reset();
        if (this.choicesInstanceTwo) {
          this.choicesInstanceTwo.clearStore();
          this.choicesInstanceTwo = null; // force re-init next time
          this.showAlert = false;
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
    const createRoleModelObject: createRoleModel = new createRoleModel(this.createRoleForm.get('roleList')?.value, this.createRoleForm.get('permissionName')?.value);

    if (this.isEditMode) {
      this.superAdminServiceObject.updateRoleById(Number(this.editModeId), createRoleModelObject).subscribe({
        next: (res) => {// console.log(res);
          this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');
          this.router.navigate([allRoutes.manageRoles]);
        },
        error: (e) => {// console.log(e);
          if (e.status == 400) { this.commonFunctionObject.openSnackBar(e.error.message, 'danger'); }
          else
            if (e.status == 422) { this.commonFunctionObject.openSnackBar(this.errorObject.directDisplayErrorMessageStatus406(JSON.parse(JSON.stringify(e.error))), 'danger'); }
        },
      });
    } else {
      this.superAdminServiceObject.createRole(createRoleModelObject).subscribe({
        next: (res) => {// console.log(res);
          this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');
        },
        error: (e) => {// console.log(e);
          if (e.status == 400) { this.commonFunctionObject.openSnackBar(e.error.message, 'danger'); }
          else
            if (e.status == 422) { this.commonFunctionObject.openSnackBar(this.errorObject.directDisplayErrorMessageStatus406(JSON.parse(JSON.stringify(e.error))), 'danger'); }
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




  }

  async ngAfterViewInit() {
    if (this.isBrowser && this.dropdownElement) {// Initialize Role dropdown immediately
      this.choicesInstance = await this.commonFunctionObject.selectDropDownConfigWithChoicesJs(this.dropdownElement, "Please select Role Name", "Type to search here...");

      this.dropdownElement.nativeElement.addEventListener('change', (event: any) => {
        this.createRoleForm.get('roleList')?.setValue(event.target.value);
        this.createRoleForm.get('roleList')?.updateValueAndValidity();
      });

      if (this.isEditMode) {
        this.superAdminServiceObject.getRoleById(Number(this.editModeId)).subscribe({
          next: (res) => {
            let responseData = JSON.parse(JSON.stringify(res)).data;

            this.createRoleForm.get('roleList')?.setValue(responseData.name, { emitEvent: true });

            this.showSecondOption = true;
            this.cdr.detectChanges();

            this.choicesInstance.setChoiceByValue(responseData.name);
            this.pendingPermissionName = responseData.permission[0].name;

            if (this.choicesInstanceTwo) {
              this.choicesInstanceTwo.setChoiceByValue(this.pendingPermissionName);
              this.createRoleForm.get('permissionName')?.setValue(this.pendingPermissionName);
            }
          },
          error: (e) => {
            this.router.navigate([allRoutes.notFound]);
          },
        });



      }
    }

    if (this.choicesInstanceTwo) {
      this.choicesInstanceTwo.clearStore();
      this.choicesInstanceTwo = null; // force re-init next time
      // this.showAlert = false;
    }
  }

  get f(): { [key: string]: AbstractControl } { return this.createRoleForm.controls; }

  private calculateDisplayErrorsForAlertBox() {
    this.displayAlertErrorList = [];
    Object.keys(this.createRoleForm.controls).forEach(controlsName => {
      const errors = this.f[controlsName]?.errors;
      if (errors) {
        switch (controlsName) {
          case 'roleList':
            errors['required'] ? this.displayAlertErrorList.push('Role Name is required.') : null;
            break;
          case 'permissionName':
            errors['required'] ? this.displayAlertErrorList.push('Permission Name is required.') : null;
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
