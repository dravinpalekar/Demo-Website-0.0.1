import { Component, EventEmitter, Output } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-side-bar',
  imports: [RouterModule],
  templateUrl: './side-bar.html',
  styleUrl: './side-bar.scss',
})
export class SideBar {

  @Output() pageTitle = new EventEmitter<string>();

  constructor() { }

  ngOnInit(): void {
    console.log("----Side-bar-menu-User component running--------ngOnInit------");
  }


  changeTitle(title: string): void {
    this.pageTitle.emit(title);
  }
}
