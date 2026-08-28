import { ChangeDetectorRef, Component, ElementRef, inject, OnInit, PLATFORM_ID, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSort, MatSortModule, Sort } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { getRolesResponseModel } from '../../../../../model/responseModel/getRolesResponseModel';
import { SuperAdminService } from '../../../../../service/superAdmin/super-admin-service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { allRoutes } from '../../../../../utils/allRoutes/allRoutes';
import { Router } from '@angular/router';
import { DialogBox } from '../../../../../utils/dialog-box/dialog-box';
import { CommonFun } from '../../../../../utils/helper/CommonFun';
import { DatePipe, isPlatformBrowser } from '@angular/common';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AlertMessage } from '../layout/alert-message/alert-message';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';

@Component({
  selector: 'app-manage-role',
  imports: [MatTableModule, MatPaginatorModule, MatSortModule, MatFormFieldModule, MatInputModule, MatIconModule, DialogBox, DatePipe, AlertMessage, ReactiveFormsModule, NgxUiLoaderModule],
  templateUrl: './manage-role.html',
  styleUrl: './manage-role.scss',
})
export class ManageRole implements OnInit {

  private platformId = inject(PLATFORM_ID);
  private ngxLoader = inject(NgxUiLoaderService);

  showModal = false;
  selectedId: number | null = null;
  modalTitle = 'Delete Record';
  modalMessage = 'Are you sure you want to delete this record?';
  displayedColumns: string[] = ['id', 'roleName', 'permissionName', 'created', 'actions'];
  dataSource = new MatTableDataSource<getRolesResponseModel>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  countSubmit = 0;
  private isBrowser: boolean;

  @ViewChild('columnNameFilter') dropdownElementColumnName!: ElementRef<HTMLSelectElement>;
  private choicesInstanceSearchColumnName: any;

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
    private router: Router,
    private commonFunctionObject: CommonFun,
    private cd: ChangeDetectorRef
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    console.log('----Manage-role-Super-Admin module running--------ngOnInit------');

    this.myFilterForm = this.fb.group({
      searchItem: new FormControl('', [Validators.required]),
      filterName: new FormControl('', [Validators.required]),
    });

    if (isPlatformBrowser(this.platformId)) {
      this.loadTableRolesData();
    }
  }

  async ngAfterViewInit() {
    if (this.isBrowser && this.dropdownElementColumnName) {
      this.choicesInstanceSearchColumnName = await this.commonFunctionObject.selectDropDownConfigWithChoicesJs(
        this.dropdownElementColumnName,
        "Please select Column Name",
        "Type to search here..."
      );
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
    this.loadTableRolesData();
  }

  loadTableRolesData() {
    this.ngxLoader.startLoader('table-loader');
    const filterName = this.myFilterForm?.get('filterName')?.value;
    const searchItem = this.myFilterForm?.get('searchItem')?.value;
    const sortParam = `${this.sortField},${this.sortDirection}`;

    this.SuperAdminServiceObject.getRoles(this.pageIndex, this.pageSize, filterName, searchItem, sortParam).subscribe({
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

    this.loadTableRolesData();
  }

  mapSortField(column: string): string {
    const fieldMapping: Record<string, string> = {
      'id': 'id',
      'roleName': 'name',
      'permissionName': 'permission.name',
      'created': 'createdAt'
    };
    return fieldMapping[column] || column;
  }

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadTableRolesData();
  }

  getRowNumber(index: number): number {
    return index + 1 + (this.pageIndex * this.pageSize);
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

          case 'filterName':
            this.displayAlertErrorList.push('Filter is required.');
            break;
        }
      }
    });
  }

  clearFilter(): void {
    this.myFilterForm.reset({
      searchItem: '',
      filterName: ''
    });

    this.myFilterForm.markAsPristine();
    this.myFilterForm.markAsUntouched();

    this.showAlert = false;
    this.submittedForm = false;
    this.displayAlertErrorList = [];

    if (this.choicesInstanceSearchColumnName) {
      this.choicesInstanceSearchColumnName.removeActiveItems();
      this.choicesInstanceSearchColumnName.setChoiceByValue('');
    }

    this.pageIndex = 0;
    this.sortField = 'id';
    this.sortDirection = 'DESC';

    if (this.countSubmit != 0) {
      this.loadTableRolesData();
    }
  }

  get f(): { [key: string]: AbstractControl } { return this.myFilterForm.controls; }

  openDailogForEditItem(id: number) {
    this.router.navigate([allRoutes.editRole + id]);
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

  onAlertClosed() {
    this.showAlert = false;
  }

  private deleteItem(id: number) {
    this.SuperAdminServiceObject.deleteRole(id).subscribe({
      next: (res) => {
        this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');

        if (this.dataSource.data.length === 1 && this.pageIndex > 0) {
          this.pageIndex--;
        }
        this.loadTableRolesData();
      },
      error: (e) => {
        if (e.status == 400) { this.commonFunctionObject.openSnackBar(e.error.error || e.error.message, 'danger'); }
      }
    });
  }

  ngOnDestroy() {
    if (this.isBrowser && this.choicesInstanceSearchColumnName) {
      this.choicesInstanceSearchColumnName.destroy();
    }
  }
}
