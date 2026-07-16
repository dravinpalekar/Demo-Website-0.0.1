import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-dashboard-content',
  imports: [],
  templateUrl: './dashboard-content.html',
  styleUrl: './dashboard-content.scss',
})
export class DashboardContent implements OnInit {

  ngOnInit(): void {
    console.log("----Dashboard-Content component running--------ngOnInit------");
  }
}
