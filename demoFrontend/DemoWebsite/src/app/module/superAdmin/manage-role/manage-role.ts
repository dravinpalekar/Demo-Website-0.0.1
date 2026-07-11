import { Component, ViewChild } from '@angular/core';
import { SuperAdminServices } from '../../../service/superAdmin/super-admin-services';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { getRolesResponseModel } from '../../../model/responseModel/getRolesResponseModel';

@Component({
  selector: 'app-manage-role',
  imports: [MatTableModule, MatPaginatorModule, MatSortModule, MatFormFieldModule, MatInputModule],
  templateUrl: './manage-role.html',
  styleUrl: './manage-role.scss',
})
export class ManageRole {

  displayedColumns: string[] = ['id', 'roleName', 'created'];
  dataSource = new MatTableDataSource<getRolesResponseModel>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private SuperAdminServiceObject: SuperAdminServices) {

    this.SuperAdminServiceObject.getRoles().subscribe((data) => {
      this.dataSource = new MatTableDataSource(JSON.parse(JSON.stringify(data)).data);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });
  }


  ngOnInit() {
    console.log('----Manage-role-Super-Admin module running--------ngOnInit------');

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
}
