import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard-super-admin',
  imports: [],
  templateUrl: './dashboard-super-admin.html',
  styleUrl: './dashboard-super-admin.scss',
})
export class DashboardSuperAdmin {

  ngOnInit(): void {
    console.log("----Dashboard Super module running--------ngOnInit------");
  }
}
