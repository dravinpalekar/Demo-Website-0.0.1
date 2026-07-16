import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NgxUiLoaderModule, NgxUiLoaderHttpModule, NgxUiLoaderRouterModule } from 'ngx-ui-loader';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,
    NgxUiLoaderModule,
    NgxUiLoaderHttpModule,
    NgxUiLoaderRouterModule,
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('demoFrontend');
}
