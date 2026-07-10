import { Component, Inject, ViewEncapsulation } from '@angular/core';
import { MatSnackBarRef, MAT_SNACK_BAR_DATA } from '@angular/material/snack-bar';

@Component({
  selector: 'app-snackbar',
  imports: [],
  templateUrl: './snackbar.html',
  styleUrl: './snackbar.scss'
})
export class Snackbar {
  constructor(public snackBarRef: MatSnackBarRef<Snackbar>,@Inject(MAT_SNACK_BAR_DATA) public data: any) { }

  ngOnInit(): void {
    console.log("----Snackbar module running--------ngOnInit------");
  }
}
