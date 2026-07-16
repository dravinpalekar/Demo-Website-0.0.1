import { Component, ElementRef, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-alert-message',
  imports: [],
  templateUrl: './alert-message.html',
  styleUrl: './alert-message.scss',
})
export class AlertMessage implements OnInit{

  @Output() closed = new EventEmitter<Boolean>();
  @Input()  message:string[] = [];
  isLeaving = false;

  constructor(private el: ElementRef) { }

  ngOnInit(): void {
    console.log('----Alert-message-Super-Admin component running--------ngOnInit------');
  }

  // 👈 Yeh lifecycle hook naye inputs aane par state reset kar dega
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['message']) {
      this.isLeaving = false; 
    }
  }

  close() {
    this.isLeaving = true;
  }

  onAnimationEnd(event: AnimationEvent) {
    if (event.animationName === 'slideOut') {
      this.closed.emit();
    }
  }
}
