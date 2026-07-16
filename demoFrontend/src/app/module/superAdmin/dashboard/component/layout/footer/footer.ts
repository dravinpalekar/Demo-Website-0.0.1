import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-footer',
  imports: [],
  templateUrl: './footer.html',
  styleUrl: './footer.scss',
})
export class Footer implements OnInit {

  ngOnInit(): void {
    console.log("----Footer-super-admin component  running--------ngOnInit------");
  }
}
