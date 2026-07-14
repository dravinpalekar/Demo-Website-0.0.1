declare var AOS: any;

import { isPlatformBrowser } from '@angular/common';
import {
  Component,
  DOCUMENT,
  Inject,
  PLATFORM_ID,
  Renderer2,
} from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonFunction } from '../../utils/helper/CommonFunction';

@Component({
  selector: 'app-landing-page',
  imports: [RouterModule],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.scss',
})
export class LandingPage {
  isBrowser: boolean = false;

  // This component is responsible for loading the landing page and its associated styles and scripts.
  constructor(
    private renderer: Renderer2,
    @Inject(DOCUMENT) private document: Document,
    @Inject(PLATFORM_ID) private platformId: Object,
    private commonFunctionObject: CommonFunction

  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {

    console.log("----Landing module running--------ngOnInit------");

    this.commonFunctionObject.loadStyle(this.renderer, 'assets/landingPageModule/vendor/aos/aos.css');
    this.commonFunctionObject.loadStyle(this.renderer, 'assets/landingPageModule/vendor/glightbox/css/glightbox.min.css');
    this.commonFunctionObject.loadStyle(this.renderer, 'assets/landingPageModule/vendor/swiper/swiper-bundle.min.css');
    this.commonFunctionObject.loadStyle(this.renderer, 'assets/landingPageModule/css/main.css');

    this.commonFunctionObject.loadScriptWithOnLoadCallback(this.renderer, 'assets/landingPageModule/vendor/aos/aos.js', () => {
      if (typeof AOS !== 'undefined') {
        AOS.init({
          // duration: 1000, // Optional: animation duration
          // once: true,     // Optional: only animate once
          duration: 600,
          easing: 'ease-in-out',
          once: true,
          mirror: false
        });
      }
    });
    this.commonFunctionObject.loadScript(this.renderer, 'assets/landingPageModule/vendor/php-email-form/validate.js');
    this.commonFunctionObject.loadScript(this.renderer, 'assets/landingPageModule/vendor/glightbox/js/glightbox.min.js');
    this.commonFunctionObject.loadScript(this.renderer, 'assets/landingPageModule/vendor/purecounter/purecounter_vanilla.js');
    this.commonFunctionObject.loadScript(this.renderer, 'assets/landingPageModule/vendor/imagesloaded/imagesloaded.pkgd.min.js');
    this.commonFunctionObject.loadScript(this.renderer, 'assets/landingPageModule/vendor/isotope-layout/isotope.pkgd.min.js');
    this.commonFunctionObject.loadScript(this.renderer, 'assets/landingPageModule/vendor/swiper/swiper-bundle.min.js');
    this.commonFunctionObject.loadScript(this.renderer, 'assets/landingPageModule/js/main.js');
  }

}
