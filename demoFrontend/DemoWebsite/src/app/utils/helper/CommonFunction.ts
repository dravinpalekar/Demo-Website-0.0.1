import { MatSnackBar } from '@angular/material/snack-bar';
import { Snackbar } from '../snackbar/snackbar';
import { DOCUMENT, ElementRef, Inject, Injectable, Renderer2 } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class CommonFunction {

  // Track which IDs we injected so we can remove them
  public styleIds = new Set<string>();
  public scriptIds = new Set<string>();
  // Track which IDs we injected so we can remove them

  constructor(private snackBarObject: MatSnackBar, @Inject(DOCUMENT) private document: Document) { }

  public openSnackBar(displayData: any, activeClassName: string) {
    this.snackBarObject.openFromComponent(Snackbar, {
      data: displayData,
      panelClass: [activeClassName],
      horizontalPosition: 'right',
      verticalPosition: 'top',
      duration: 1000 * 4,
    });
  }

//   public async selectDropDownConfigWithChoicesJs(dropdownElement: ElementRef<HTMLSelectElement>, placeholderValue: string, searchPlaceholderValue: string ){
//     // Dynamically import Choices.js only in browser
//      const Choices = (await import('choices.js')).default;
//       return new Choices(dropdownElement.nativeElement, {
//         placeholder: true,
//         placeholderValue: placeholderValue,
//         searchEnabled: true,
//         searchPlaceholderValue: searchPlaceholderValue,
//         itemSelectText: '',
//         removeItemButton: true
//       });
//   }

//   public loadStyle(renderer: Renderer2, href: string): void {
//     const linkEl = renderer.createElement('link');
//     linkEl.rel = 'stylesheet';
//     linkEl.href = href;
//     renderer.appendChild(this.document.head, linkEl);
//   }


//   public loadScript(renderer: Renderer2, src: string): void {
//     const scriptEl = renderer.createElement('script');
//     scriptEl.src = src;
//     scriptEl.async = true;
//     renderer.appendChild(this.document.body, scriptEl);
//   }

//   public loadStyleAndStoreStyleId(renderer: Renderer2, href: string, id: string): void {
//     const linkEl = renderer.createElement('link');
//     linkEl.id = id;
//     linkEl.rel = 'stylesheet';
//     linkEl.href = href;
//     renderer.appendChild(this.document.head, linkEl);
//     this.styleIds.add(id);
//   }

//   public loadScriptWithOnLoadCallback(renderer: Renderer2, src: string, onLoadCallback?: () => void): void {
//     const scriptEl = renderer.createElement('script');
//     scriptEl.src = src;
//     scriptEl.async = true;
//     if (onLoadCallback) {
//       scriptEl.onload = onLoadCallback;
//     }
//     renderer.appendChild(this.document.body, scriptEl);
//   }

//   public removeCssAndJsFileById(renderer: Renderer2, id: string): void {
//     const el = this.document.getElementById(id);
//     if (el && el.parentNode) {
//       renderer.removeChild(el.parentNode, el);
//     }
//   }
}