import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.css'],
  standalone: true,
  imports: [
    CommonModule
  ]


})
export class ConfirmationDialogComponent {


  //@Input() permitira recibir informacion del componente padre
  // aqui, el padre nos dira que mensaje queremos mostrar
  @Input() message: string = '¿Estas seguro que deseas realizar esta acción ?';

  //@Output() permitira enviar informacion al componente padre
  // confirm, emitira un evento de confirmacion al padre
  @Output() confirm = new EventEmitter<void>();

  @Output() cancel = new EventEmitter<void>();



  onConfirm(): void {
    this.confirm.emit();
  }

  onCancel(): void {
    this.cancel.emit();
  }


}
