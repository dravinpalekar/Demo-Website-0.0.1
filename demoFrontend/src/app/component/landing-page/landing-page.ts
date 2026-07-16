declare var AOS: any;

import { Component, inject, OnInit, Renderer2 } from '@angular/core';
import { CommonFun } from '../../utils/helper/CommonFun';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-landing-page',
  imports: [RouterModule],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.scss',
})
export class LandingPage implements OnInit{

  private commonFunObject: CommonFun = inject(CommonFun);

  private renderer: Renderer2 = inject(Renderer2);

    ngOnInit(): void {

    console.log("----Landing-page component running--------ngOnInit------");

    this.commonFunObject.loadStyle(this.renderer, 'assets/landingPageModule/vendor/aos/aos.css');
    this.commonFunObject.loadStyle(this.renderer, 'assets/landingPageModule/vendor/glightbox/css/glightbox.min.css');
    this.commonFunObject.loadStyle(this.renderer, 'assets/landingPageModule/vendor/swiper/swiper-bundle.min.css');
    this.commonFunObject.loadStyle(this.renderer, 'assets/landingPageModule/css/main.css');

    this.commonFunObject.loadScriptWithOnLoadCallback(this.renderer, 'assets/landingPageModule/vendor/aos/aos.js', () => {
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
    this.commonFunObject.loadScript(this.renderer, 'assets/landingPageModule/vendor/php-email-form/validate.js');
    this.commonFunObject.loadScript(this.renderer, 'assets/landingPageModule/vendor/glightbox/js/glightbox.min.js');
    this.commonFunObject.loadScript(this.renderer, 'assets/landingPageModule/vendor/purecounter/purecounter_vanilla.js');
    this.commonFunObject.loadScript(this.renderer, 'assets/landingPageModule/vendor/imagesloaded/imagesloaded.pkgd.min.js');
    this.commonFunObject.loadScript(this.renderer, 'assets/landingPageModule/vendor/isotope-layout/isotope.pkgd.min.js');
    this.commonFunObject.loadScript(this.renderer, 'assets/landingPageModule/vendor/swiper/swiper-bundle.min.js');
    this.commonFunObject.loadScript(this.renderer, 'assets/landingPageModule/js/main.js');
  }
}
