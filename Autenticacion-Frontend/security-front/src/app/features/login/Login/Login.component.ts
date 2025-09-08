import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-Login',
  templateUrl: './Login.component.html',
  styleUrls: ['./Login.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule]
})
export class LoginComponent implements OnInit {

  // el ! es el operador de "no nulo" que no permite que la variable se inicialice sin un valor
  loginForm!: FormGroup;

  // ? es el operador de "opcional" que permite que la variable se inicialice con un valor o no.
  errorMessage?: string;



  // el FormBuilder es un servicio que nos permite crear formularios de una manera
  // mas sencilla y eficiente. Nos permite definir los campos del formulario y
  // sus validaciones de una manera declarativa.
  private fb = inject(FormBuilder);
  private authSerive = inject(AuthService);
  private router = inject(Router);


  ngOnInit(): void {

    this.loginForm = this.fb.group({

      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(3)]]

    });
  }
  onSubmit(): void {
    if (this.loginForm.invalid) {
      return; // Si el formulario no es válido, no hacemos nada.
    }

    // Limpiamos errores previos
    this.errorMessage = undefined;

    // Llamamos al método de login de nuestro servicio
    this.authSerive.login(this.loginForm.value).subscribe({
      next: (response) => {
        // ¡Éxito!
        console.log('Login exitoso!', response);
        // Redirigimos al usuario a la página de productos.
        this.router.navigate(['/products']);
      },
      error: (err) => {
        // ¡Error!
        console.error('Error en el login', err);
        this.errorMessage = 'Usuario o contraseña incorrectos. Por favor, inténtelo de nuevo.';
      }
    });
  }
}

