import { Component, OnInit, ViewChild } from '@angular/core';
import { SuperAdminService } from '../../../../../service/superAdmin/super-admin-service';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { getRolesResponseModel } from '../../../../../model/responseModel/getRolesResponseModel';
import { MatIconButton } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonFun } from '../../../../../utils/helper/CommonFun';

@Component({
  selector: 'app-manage-permission',
  imports: [MatTableModule, MatPaginatorModule, MatSortModule, MatFormFieldModule, MatInputModule, MatIconModule,],
  templateUrl: './manage-permission.html',
  styleUrl: './manage-permission.scss',
})
export class ManagePermission implements OnInit {


  displayedColumns: string[] = ['id', 'permissionName', 'created', 'actions'];
  dataSource = new MatTableDataSource<getRolesResponseModel>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private SuperAdminServiceObject: SuperAdminService, private commonFunctionObject: CommonFun) {

    this.SuperAdminServiceObject.getPermissions().subscribe((data) => {
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


  startEdit(id: number) {
    alert(id);
  }

  deleteItem(id: number) {

    this.SuperAdminServiceObject.deletePermission(id).subscribe({
      next:(res) => {
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

}
