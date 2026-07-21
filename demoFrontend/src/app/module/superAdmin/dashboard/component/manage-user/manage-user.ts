import { Component, OnInit, ViewChild } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { DialogBox } from '../../../../../utils/dialog-box/dialog-box';
import { getRolesResponseModel } from '../../../../../model/responseModel/getRolesResponseModel';
import { SuperAdminService } from '../../../../../service/superAdmin/super-admin-service';
import { allRoutes } from '../../../../../utils/allRoutes/allRoutes';
import { CommonFun } from '../../../../../utils/helper/CommonFun';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-manage-user',
  imports: [MatTableModule, MatPaginatorModule, MatSortModule, MatFormFieldModule, MatInputModule, MatIconModule, DialogBox, DatePipe],
  templateUrl: './manage-user.html',
  styleUrl: './manage-user.scss',
})
export class ManageUser implements OnInit {

  showModal = false;
  modalTitle = '';
  modalMessage = '';
  activateDeactivate = false;
  selectedId: number | null = null;
  active!: string;
  displayedColumns: string[] = ['id', 'fullName', 'emailAddress', 'roleName', 'permissionName', 'age', 'gender', 'country', 'status', 'created', 'actions'];
  dataSource = new MatTableDataSource<getRolesResponseModel>([]);

  Boolean = Boolean;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private SuperAdminServiceObject: SuperAdminService, private commonFunctionObject: CommonFun, private router: Router,) {

    this.SuperAdminServiceObject.getUsers().subscribe((data) => {
      this.dataSource = new MatTableDataSource(JSON.parse(JSON.stringify(data)).data);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });
  }

  ngOnInit() {
    console.log('----Manage-permission-Super-Admin component running--------ngOnInit------');
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage(); // always go back to page 1
    }
  }

  getRowNumber(index: number): number {

    if (this.paginator) {
      return index + 1 + (this.paginator.pageIndex * this.paginator.pageSize);
    }
    return index + 1;
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  openDailogForActivateAndDeactivateItem(id: number, active: string) {
    this.selectedId = id;
    this.active = active;
    let prepareString = (active == 'ENABLE' ? 'Disable' : 'Enable');
    this.modalTitle = prepareString + ' Record';
    this.modalMessage = 'Are you sure you want to ' + prepareString.toLowerCase() + ' this record?';
    this.activateDeactivate = true;
    this.showModal = true;
  }

  openDailogForDeteteItem(id: number) {
    this.selectedId = id;
    this.modalTitle = 'Delete Record';
    this.modalMessage = 'Are you sure you want to delete this record?';
    this.showModal = true;
  }

  onConfirm(result: boolean) {
    this.showModal = false;
    if (result && this.selectedId !== null && this.activateDeactivate == false) {
      this.deleteItem(this.selectedId);
    }
    if (result && this.selectedId !== null && this.activateDeactivate == true) {
      this.activateOrDeactivate(this.selectedId, this.active);
      this.activateDeactivate = false;
    }
    this.selectedId = null;
  }

  closeModal() {
    this.showModal = false;
    this.selectedId = null;
  }

  private deleteItem(id: number) {

    this.SuperAdminServiceObject.deleteUserById(id).subscribe({
      next: (res) => {
        this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');
        // Filter out the deleted record from the current dataset
        const currentData = this.dataSource.data;
        this.dataSource.data = currentData.filter(item => item.id !== id);

        // Re-assign paginator and sort to keep them working properly
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (e) => {
        if (e.status == 400) { this.commonFunctionObject.openSnackBar(e.error.error, 'danger'); }
      }
    });
  }

  private activateOrDeactivate(id: number, active: string) {

    const newStatus = active === "DISABLE" ? "ENABLE" : "DISABLE";

    const requestData = { "id": id, status: newStatus };

    this.SuperAdminServiceObject.activateDeactivate(requestData).subscribe({
      next: (res) => {
        this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');
        const currentData = this.dataSource.data;
        const index = currentData.findIndex(item => item.id === id);
        if (index !== -1) {
          currentData[index].active = newStatus; // Update property
          this.dataSource.data = [...currentData]; // Spread operator se nai array reference assign karein
        }

        // Re-assign paginator and sort to keep them working properly
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (e) => {
        if (e.status == 400) { this.commonFunctionObject.openSnackBar(e.error.error, 'danger'); }
      }
    })
  }

}

