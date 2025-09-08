import { computed, inject, Injectable, signal } from '@angular/core';
import axios from 'axios';
import { UserDto } from '../models/user.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest } from '../models/auth.model';
import { Observable, tap } from 'rxjs';



// el Injectable es un decorador que nos permite inyectar el servicio en cualquier lugar de la aplicacion
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  // // # numereral es para indicar que es una variable privada detal modo que no se puede acceder desde afuera de la clase
  // #currenUser = signal<UserDto | null | undefined>(undefined);


  // isAuthenticated = computed(() => !!this.#currenUser());

  // currentUser = computed(() => this.#currenUser());


  // el readonly es para indicar que la variable es de solo lectura
  private readonly apiUrl = environment.apiUrl;

  // el inject  es un decorador que nos permite inyectar el servicio en cualquier lugar de la aplicacion
  private htt = inject(HttpClient);

  constructor() { }

  /**
   * Inicia sesion con el servidor, guardando el token en el localStorage
   * @param credential Credenciales de inicio de sesion
   * @returns Un observable que emite la respuesta del servidor
   *          con la informacion de la sesion iniciada y el token de acceso ejemplo: { accessToken: 'token' }
   */
  login(credential: LoginRequest): Observable<AuthResponse> {

    return this.htt.post<AuthResponse>(`${this.apiUrl}/auth/login`, credential).pipe(

      // el tap es un operador que nos permite manipular la respuesta del servidor
      tap(respose => {
        localStorage.setItem('accessToken', respose.accessToken)
      })
    );
  }

  logout(): void {
    localStorage.removeItem('accessToken');
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('accessToken');
  }




}
