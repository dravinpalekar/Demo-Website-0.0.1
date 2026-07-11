import { Component, EventEmitter, Output } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-side-bar-menu',
  imports: [RouterModule],
  templateUrl: './side-bar-menu.html',
  styleUrl: './side-bar-menu.scss',
})
export class SideBarMenu {

  @Output() pageTitle = new EventEmitter<string>();


  constructor() { }

  ngOnInit(): void {
    console.log("----Side-bar-menu-Super-Admin module running--------ngOnInit------");

  }


  changeTitle(title: string): void {
    this.pageTitle.emit(title);
  }
}
