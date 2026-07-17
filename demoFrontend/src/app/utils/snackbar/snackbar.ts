import { Component, Inject, OnInit } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';

@Component({
  selector: 'app-snackbar',
  imports: [],
  templateUrl: './snackbar.html',
  styleUrl: './snackbar.scss',
})
export class Snackbar implements OnInit{

  constructor(public snackBarRef: MatSnackBarRef<Snackbar>,@Inject(MAT_SNACK_BAR_DATA) public data: string) { }
  ngOnInit(): void {
    console.log("----Snackbar component running--------ngOnInit------");
    //   setTimeout(()=>{
    //   this.run="leave";
    // },2600);
  }
}
