import { Component, ElementRef, EventEmitter, Inject, Input, OnInit, Output, PLATFORM_ID, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonFun } from '../../../../../utils/helper/CommonFun';
import { Errors } from '../../../../../utils/helper/Errors';
import { SuperAdminService } from '../../../../../service/superAdmin/super-admin-service';
import { isPlatformBrowser } from '@angular/common';
import { createNameModel } from '../../../../../model/requestModel/superAdmin/createNameModel';
import { AlertMessage } from '../layout/alert-message/alert-message';
import { ActivatedRoute, Router } from '@angular/router';
import { allRoutes } from '../../../../../utils/allRoutes/allRoutes';

@Component({
  selector: 'app-create-permission',
  imports: [ReactiveFormsModule, AlertMessage],
  templateUrl: './create-permission.html',
  styleUrl: './create-permission.scss',
})
export class CreatePermission implements OnInit {

  @ViewChild('dropdown') dropdownElement!: ElementRef<HTMLSelectElement>;
  @Input() closed: Boolean = false;
  @Output() pageTitle = new EventEmitter<string>();
  isEditMode: boolean = false;
  editModeId: number | undefined;
  createPermissionForm!: FormGroup;
  submittedForm = false;
  showAlert = true;
  private choicesInstance: any;
  private isBrowser: boolean;
  displayAlertErrorList: string[] = [];

  constructor(
    @Inject(PLATFORM_ID) platformId: object, private formBuilderObject: FormBuilder, private router: Router, private route: ActivatedRoute,
    private superAdminServiceObject: SuperAdminService, private commonFunctionObject: CommonFun, private errorObject: Errors) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  ngOnInit() {

    console.log('----Create-permission-Super-Admin component running--------ngOnInit------');
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

    if (this.isEditMode) {
      this.superAdminServiceObject.updatePermissionById(Number(this.editModeId), createPermissionModelObject).subscribe({
        next: (res) => {// console.log(res);
          this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');
        },
        error: (e) => {// console.log(e);
          if (e.status == 400) {
            this.commonFunctionObject.openSnackBar(e.error.accessDeniedReason, 'danger');
          } else if (e.status == 406) {
            this.commonFunctionObject.openSnackBar(this.errorObject.errorStatus406(JSON.parse(JSON.stringify(e.error))), 'danger');
          }
        },
      })
    } else {
      this.superAdminServiceObject.createPermission(createPermissionModelObject).subscribe({
        next: (res) => {// console.log(res);
          this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');
        },
        error: (e) => {// console.log(e);
          if (e.status == 400) {
            this.commonFunctionObject.openSnackBar(e.error.accessDeniedReason, 'danger');
          } else if (e.status == 406) {
            this.commonFunctionObject.openSnackBar(this.errorObject.errorStatus406(JSON.parse(JSON.stringify(e.error))), 'danger');
          }
        },
      });
    }


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

      this.route.paramMap.subscribe(params => {
        const id = params.get('id');
        if (id) {
          this.isEditMode = true;
          this.editModeId = Number(id);
          this.pageTitle.emit("Edit Permission");
          this.superAdminServiceObject.getPermissionById(Number(id)).subscribe({
            next: (res) => {
              this.choicesInstance.setChoiceByValue(JSON.parse(JSON.stringify(res)).data.name);
            },
            error: (e) => {
              this.router.navigate([allRoutes.notFound]);
            },
          });
        }
      });
    }
  }

  get f(): { [key: string]: AbstractControl } { return this.createPermissionForm.controls; }

  private calculateDisplayErrorsForAlertBox() {
    this.displayAlertErrorList = [];
    Object.keys(this.createPermissionForm.controls).forEach(controlsName => {
      const errors = this.f[controlsName]?.errors;
      if (errors) {
        switch (controlsName) {
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
  }
}
