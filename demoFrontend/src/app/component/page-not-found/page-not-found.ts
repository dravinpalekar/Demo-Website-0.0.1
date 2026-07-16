import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-page-not-found',
  imports: [],
  templateUrl: './page-not-found.html',
  styleUrl: './page-not-found.scss',
})
export class PageNotFound implements OnInit{

  ngOnInit(): void {

    console.log('----Page-not-found component running--------ngOnInit------');

    }
}
