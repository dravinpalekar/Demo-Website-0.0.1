import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-dialog-box',
  imports: [],
  templateUrl: './dialog-box.html',
  styleUrl: './dialog-box.scss',
})
export class DialogBox {

  @Input() title = '';
  @Input() message = '';
  @Input() btnOkText = 'OK';
  @Input() btnCancelText = 'Cancel';

  constructor() { }


  @Output() confirmed = new EventEmitter<boolean>();
  @Output() closed = new EventEmitter<void>();

  accept(): void {
    this.confirmed.emit(true);
  }

  decline(): void {
    this.confirmed.emit(false);
  }

  dismiss(): void {
    this.closed.emit();
  }
}
