import { Component, Inject } from '@angular/core';
import { MatSnackBarRef, MAT_SNACK_BAR_DATA } from '@angular/material/snack-bar';
import { trigger, transition, style, animate } from '@angular/animations';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-snackbar',
  imports: [MatIconModule],
  templateUrl: './snackbar.html',
  styleUrl: './snackbar.scss',
  animations: [
    trigger('fade', [
      // state('enter', style({ transform: 'translateX(100%)'})),
      // state('leave', style({ transform: 'translateX(-100%)'})),
      // transition('enter => leave', [animate(400)])
      transition(':enter', [
        style({ transform: 'translateX(100%)' }),
        animate('400ms ease-in', style({ transform: 'translateX(0%)' })),
        // animate('400ms ease-out', style({ transform: 'translateX(50%)' })),
      ]),
      // transition(':leave', [
      //   style({ transform: 'translateX(0%)' }),
      //   animate('400ms ease-in', style({ transform: 'translateX(100%)' })),
      //   // animate('400ms ease-out', style({ transform: 'translateX(50%)' })),
      // ]),
      // transition('enter => leave', [
      //   // style({ transform: 'translateX(0%)' }),
      //   animate('400ms ease-out')
      //   // animate('400ms ease-out', style({ transform: 'translateX(50%)' })),
      // ]),

    ])
  ]
})
export class Snackbar {

  run = "enter";
  constructor(public snackBarRef: MatSnackBarRef<Snackbar>,@Inject(MAT_SNACK_BAR_DATA) public data: string) { }
  ngOnInit(): void {
    console.log("----Snackbar module running--------ngOnInit------");
    //   setTimeout(()=>{
    //   this.run="leave";
    // },2600);
  }
}
