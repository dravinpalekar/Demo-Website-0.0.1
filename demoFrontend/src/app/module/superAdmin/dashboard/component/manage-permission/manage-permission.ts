import { Component, inject, OnInit, PLATFORM_ID, ViewChild } from '@angular/core';
import { SuperAdminService } from '../../../../../service/superAdmin/super-admin-service';
import { MatSort, MatSortModule, Sort } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { getRolesResponseModel } from '../../../../../model/responseModel/getRolesResponseModel';
import { MatIconModule } from '@angular/material/icon';
import { CommonFun } from '../../../../../utils/helper/CommonFun';
import { DialogBox } from '../../../../../utils/dialog-box/dialog-box';
import { Router } from '@angular/router';
import { allRoutes } from '../../../../../utils/allRoutes/allRoutes';
import { DatePipe, isPlatformBrowser } from '@angular/common';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AlertMessage } from '../layout/alert-message/alert-message';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';

@Component({
  selector: 'app-manage-permission',
  imports: [MatTableModule, MatPaginatorModule, MatSortModule, MatFormFieldModule, MatInputModule, MatIconModule, DialogBox, DatePipe, AlertMessage, ReactiveFormsModule, NgxUiLoaderModule],
  templateUrl: './manage-permission.html',
  styleUrl: './manage-permission.scss',
})
export class ManagePermission implements OnInit {

  private platformId = inject(PLATFORM_ID);
  private ngxLoader = inject(NgxUiLoaderService);

  showModal = false;
  modalTitle = 'Delete Record';
  modalMessage = 'Are you sure you want to delete this record?';
  selectedId: number | null = null;
  displayedColumns: string[] = ['id', 'permissionName', 'created', 'actions'];
  dataSource = new MatTableDataSource<getRolesResponseModel>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  countSubmit = 0;
  displayAlertErrorList: string[] = [];
  myFilterForm!: FormGroup;
  submittedForm = false;
  showAlert = false;

  // Sort properties
  sortField = 'id';
  sortDirection = 'DESC';

  // Pagination properties
  totalElements = 0;
  pageSize = 10;
  pageIndex = 0;

  constructor(
    private fb: FormBuilder,
    private SuperAdminServiceObject: SuperAdminService,
    private commonFunctionObject: CommonFun,
    private router: Router
  ) {

  }

  ngOnInit() {
    console.log('----Manage-permission-Super-Admin component running--------ngOnInit------');

    this.myFilterForm = this.fb.group({
      searchItem: new FormControl('', [Validators.required]),
    });

    if (isPlatformBrowser(this.platformId)) {
      this.loadTablePermissionsData();
    }
  }

  onSubmit() {
    this.countSubmit++;
    this.submittedForm = true;

    if (this.myFilterForm.invalid) {
      this.showAlert = true;
      this.calculateDisplayErrorsForAlertBox();
      return;
    }

    this.showAlert = false;
    this.pageIndex = 0;
    if (this.paginator) {
      this.paginator.pageIndex = 0;
    }
    this.loadTablePermissionsData();
  }

  loadTablePermissionsData() {
    this.ngxLoader.startLoader('table-loader');
    const searchItem = this.myFilterForm?.get('searchItem')?.value;
    const sortParam = `${this.sortField},${this.sortDirection}`;

    this.SuperAdminServiceObject.getPermissions(this.pageIndex, this.pageSize, searchItem, sortParam).subscribe({
      next: (response: any) => {
        this.dataSource.data = response.data;
        this.totalElements = response.getTotalElements;
        this.ngxLoader.stopLoader('table-loader');
      },
      error: (err) => {
        console.error(err);
        this.ngxLoader.stopLoader('table-loader');
      }
    });
  }

  private calculateDisplayErrorsForAlertBox() {
    this.displayAlertErrorList = [];
    Object.keys(this.myFilterForm.controls).forEach(controlsName => {
      const errors = this.f[controlsName]?.errors;
      if (errors) {
        switch (controlsName) {
          case 'searchItem':
            errors['required'] && this.displayAlertErrorList.push('Search item is required.');
            break;
        }
      }
    });
  }

  clearFilter(): void {
    // Angular Reactive Form reset
    this.myFilterForm.reset({
      searchItem: ''
    });

    // Form ko pristine state mein rakho
    this.myFilterForm.markAsPristine();
    this.myFilterForm.markAsUntouched();

    // Validation alert clear
    this.showAlert = false;
    this.submittedForm = false;
    this.displayAlertErrorList = [];

    // First page
    this.pageIndex = 0;

    this.sortField = 'id';
    this.sortDirection = 'DESC';

    // All permissions load
    if (this.countSubmit != 0) {
      this.loadTablePermissionsData();
    }
  }

  get f(): { [key: string]: AbstractControl } { return this.myFilterForm.controls; }

  onAlertClosed() {
    this.showAlert = false;
  }

  onSortChange(sort: Sort) {
    if (!sort.active || sort.direction === '') {
      this.sortField = 'id';
      this.sortDirection = 'DESC';
    } else {
      this.sortField = this.mapSortField(sort.active);
      this.sortDirection = sort.direction.toUpperCase();
    }

    this.pageIndex = 0;
    if (this.paginator) {
      this.paginator.pageIndex = 0;
    }

    this.loadTablePermissionsData();
  }

  mapSortField(column: string): string {
    const fieldMapping: Record<string, string> = {
      'id': 'id',
      'permissionName': 'name',
      'created': 'createdAt'
    };
    return fieldMapping[column] || column;
  }

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadTablePermissionsData();
  }

  getRowNumber(index: number): number {
    return index + 1 + (this.pageIndex * this.pageSize);
  }

  openDailogForEditItem(id: number) {
    this.router.navigate([allRoutes.editPermission + id]);
  }

  openDailogForDeteteItem(id: number) {
    this.selectedId = id;
    this.showModal = true;
  }

  onConfirm(result: boolean) {
    this.showModal = false;
    if (result && this.selectedId !== null) {
      this.deleteItem(this.selectedId);
    }
    this.selectedId = null;
  }

  closeModal() {
    this.showModal = false;
    this.selectedId = null;
  }

  private deleteItem(id: number) {
    this.SuperAdminServiceObject.deletePermission(id).subscribe({
      next: (res) => {
        this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');

        if (this.dataSource.data.length === 1 && this.pageIndex > 0) {
          this.pageIndex--;
        }
        this.loadTablePermissionsData();
      },
      error: (e) => {
        if (e.status == 400) { this.commonFunctionObject.openSnackBar(e.error.error || e.error.message, 'danger'); }
      }
    });
  }

}
