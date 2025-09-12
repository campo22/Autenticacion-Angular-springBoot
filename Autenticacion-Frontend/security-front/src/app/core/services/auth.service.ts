import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap, finalize, of, map, catchError } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest } from '../../shared/models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl = `${environment.apiUrl}/auth`;


  private http = inject(HttpClient);
  private router = inject(Router);

  // Nuestra única fuente de verdad para el estado de autenticación
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);

  /**
   * Un Observable público al que los componentes pueden suscribirse para reaccionar
   * a los cambios en el estado de autenticación (login/logout).
   */
  public currentUser$ = this.currentUserSubject.asObservable();

  // Flag para prevenir múltiples llamadas concurrentes de refresh
  private isRefreshing = false;

  constructor() {

  }

  /**
   * Getter para acceder al valor síncrono del usuario actual.
   * Útil para comprobaciones inmediatas.
   */
  public get currentUserValue(): AuthResponse | null {
    return this.currentUserSubject.getValue();
  }

  /**
   * Getter para obtener el accessToken actual.
   * Lee el token directamente de nuestra fuente de verdad (currentUserSubject)
   * para garantizar que siempre esté actualizado.
   * Usado por el AuthInterceptor.
   */
  public get getAccessToken(): string | null {
    const currentUser = this.currentUserSubject.getValue();
    return currentUser ? currentUser.accessToken : null;
  }

  // /**
  //  * Realiza el registro de un nuevo usuario.
  //  * @param registerRequest Los datos para el registro.
  //  * @returns Un Observable con la información del usuario recién creado.
  //  */
  // register(registerRequest: RegisterRequest): Observable<UserResponse> {
  //   // La opción 'withCredentials' y el token son manejados por el interceptor.
  //   return this.http.post<UserResponse>(`${this.apiUrl}/register`, registerRequest);
  // }

  /**
   * Autentica a un usuario y gestiona la redirección.
   * @param loginRequest Las credenciales del usuario.
   * @returns Un Observable con la información de la sesión.
   */
  login(loginRequest: LoginRequest): Observable<AuthResponse> {

    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, loginRequest).pipe(
      tap(response => {
        this.currentUserSubject.next(response);
        console.log('Login exitoso, estado actualizado para:', response.username);
        this.router.navigate(['/products']); // La navegación ocurre aquí, tras el éxito.
      })
    );
  }


  logout(): void {

    this.http.post(`${this.apiUrl}/logout`, {}).pipe(
      // finalize asegura que el estado local se limpie, incluso si la llamada HTTP falla.
      finalize(() => {
        this.currentUserSubject.next(null);
        this.router.navigate(['/login']);
      })
    ).subscribe({
      next: () => console.log('Logout en backend exitoso.'),
      error: (err) => console.error('Error durante el logout en backend.', err)
    });
  }

  /**
   * Intenta refrescar la sesión silenciosamente (usado por APP_INITIALIZER).
   * @returns Un Observable<boolean> que indica si el refresco fue exitoso.
   */
  trySilentRefresh(): Observable<boolean> {
    if (this.isRefreshing) {
      return of(false); // Previene llamadas duplicadas
    }
    this.isRefreshing = true;

    // El interceptor se encargará de añadir 'withCredentials: true'
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, {}).pipe(
      tap({
        next: response => {
          this.currentUserSubject.next(response);
          console.log('Refresh silencioso exitoso.');
        },
        error: () => {
          this.currentUserSubject.next(null);
          console.log('No hay sesión activa para refrescar.');
        }
      }),
      map(() => true), // Transforma una respuesta exitosa en 'true'
      catchError(() => of(false)), // Transforma un error en 'false'
      finalize(() => this.isRefreshing = false) // Resetea el flag
    );
  }
}
