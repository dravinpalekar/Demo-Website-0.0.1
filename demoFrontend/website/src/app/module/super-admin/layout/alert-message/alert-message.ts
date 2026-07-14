import { trigger, state, style, transition, animate } from '@angular/animations';
import { Component, ElementRef, EventEmitter, Input, Output, } from '@angular/core';

@Component({
  selector: 'app-alert-message',
  imports: [],
  templateUrl: './alert-message.html',
  styleUrl: './alert-message.scss',
  animations: [
    trigger('fadeOut', [
      state('visible', style({ opacity: 1, transform: 'translate(0, 0)' })),
      state('hidden', style({ opacity: 0, transform: 'translate(100%, 0px)' })), // end position after both steps
      transition('void => visible', [
        style({ opacity: 0, transform: 'translateX(100%)' }), // start slightly right to left and invisible
        animate('0.5s ease-out') // quick fade in + slide in
      ]),
      transition('visible => hidden', [
        // Step 1: slide right
        animate('1.3s ease-out', style({ transform: 'translateX(100%)', opacity: 1 })),
        // Step 2: fade + move upward
      ])
    ])
  ]
})
export class AlertMessage {

  @Output() closed = new EventEmitter<Boolean>();
  @Input()  message:string[] = [];
  state: 'visible' | 'hidden' = 'visible';

  constructor(private el: ElementRef) { }

  ngOnInit(): void {
    console.log('----Alert-message-Super-Admin module running--------ngOnInit------');

    // this.message = this.el.nativeElement.getAttribute('data-message') || '';
    // console.log(this.message);

  }

  close() {
    this.state = 'hidden';
  }

  onFadeDone(e: any) {
    // When animation reaches 'hidden', remove the block from the DOM.
    if (e.toState === 'hidden') {
      this.closed.emit();
    }
  }

}
