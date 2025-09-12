import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Observable } from 'rxjs';
import { AuthResponse } from '../../shared/models/auth.model';

@Component({
  selector: 'app-Navbar',
  templateUrl: './Navbar.component.html',
  styleUrls: ['./Navbar.component.css'],
  standalone: true,
  imports: [
    CommonModule, // el common module es necesario para el ngIf, el ngFor,async pipe ,
    RouterLink, // el routerLink es necesario para las rutas
    RouterLinkActive // el routerLinkActive es para aplicar estilos a la ruta activa
  ],
})



export class NavbarComponent {

  isOpen = false; // control del menú móvil

  // Links dinámicos
  links = [
    { path: '/products', label: 'Productos', exact: true },

  ]

  private authService = inject(AuthService)

  currentUser$: Observable<AuthResponse | null> = this.authService.currentUser$;

  logout(): void {
    this.authService.logout();
  }

}


