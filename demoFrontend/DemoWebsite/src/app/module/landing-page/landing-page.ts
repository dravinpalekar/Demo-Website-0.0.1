import { Component } from '@angular/core';

@Component({
  selector: 'app-landing-page',
  imports: [],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.scss',
})
export class LandingPage {
   ngOnInit(): void {
    console.log("----Landing page module running--------ngOnInit------");
  }
}
