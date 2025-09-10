import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest } from '../models/auth.model';
import { BehaviorSubject, catchError, finalize, map, Observable, of, tap } from 'rxjs';
import { Router } from '@angular/router';
;



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
  private http = inject(HttpClient);
  private router = inject(Router);

  //el BehaviorSubject es un observable que nos permite manejar el estado del usuario en tiempo real
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);

  // exponemos el estado como un observable publico de solo lectura
  // los componentes se subcribiran a este observable para obtener el estado del usuario
  public currentUser$ = this.currentUserSubject.asObservable();

  // Guardamos el AccessToken en memoria. nunca en el localStorage
  private accessToken: string | null = null;

  // Flag para evitar múltiples llamadas de refresh al mismo tiempo
  private isRefreshing = false;




  /**
   * Obtiene el valor actual del usuario autenticado.
   *
   * Devuelve el valor actual del observable currentUser$.
   * Si el usuario no est  autenticado, devuelve null.
   *
   * @returns el valor actual del usuario autenticado o null si no est  autenticado.
   */
  public get currentUserValue(): AuthResponse | null {
    return this.currentUserSubject.getValue();
  }

  public get getAccessToken(): string | null {
    return this.accessToken;

  }

  login(LoginRequest: LoginRequest): Observable<AuthResponse> {

    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, LoginRequest).pipe(

      tap(response => {

        this.accessToken = response.accessToken;
        this.currentUserSubject.next(response); // actualizamos el observable con el nuevo valor
        console.log('Login exitoso!', response);

      })
    );
  }



  /**
   * Cierra la sesi n actual del usuario.
   *
   * Hace una petici n HTTP POST al endpoint /logout para que el backend invalide la cookie HttpOnly.
   * Luego, pase lo que pase ( xito o error), elimina el accessToken y el usuario actual, y redirige al usuario a la p gina de login.
   *
   * @returns un observable que se completa cuando se ha ejecutado el logout.
   */
  logout() {
    // Llamamos al endpoint de logout del backend para que invalide la cookie HttpOnly
    this.http.post(`${this.apiUrl}/auth/logout`, {}).pipe(
      finalize(() => {
        // Esto se ejecuta SIEMPRE (éxito o error)
        this.accessToken = null;
        this.currentUserSubject.next(null);
        this.router.navigate(['/login']);
      })
    ).subscribe({
      next: () => console.log('Logout exitoso en el backend'),
      error: (err) => console.error('Error en el logout', err),
      complete: () => console.log('Logout completado')
    });

  }


  /**
   * Intenta refrescar la sesi n actual en segundo plano.
   *
   * Si la sesi n actual est  vencida, intenta refrescarla llamando al endpoint /refresh.
   * Si el refresco tiene   xito, actualiza el accessToken y el usuario actual, y devuelve un observable que emite true.
   * Si el refresco falla (porque el usuario no est  autenticado), elimina el accessToken y el usuario actual, y devuelve un observable que emite false.
   *
   * Se utiliza en el APP_INITIALIZER para intentar refrescar la sesi n actual antes de que se inicie la aplicaci n.
   *
   * @returns un observable que emite true si el refresco es exitoso, o false si falla.
   */
  trySilentRefresh(): Observable<boolean> {


    if (this.isRefreshing) {
      return of(false);
    }
    this.isRefreshing = true;

    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/refresh`, {}).pipe(
      tap({
        next: response => {
          this.accessToken = response.accessToken;
          this.currentUserSubject.next(response);
          this.isRefreshing = false;
          console.log('Refresh silencioso exitoso.');
        },
        error: () => {
          this.accessToken = null;
          this.currentUserSubject.next(null);
          this.isRefreshing = false;
          console.log('No hay sesión activa para refrescar.');
        }
      }),
      // Convertimos la respuesta a un booleano para el APP_INITIALIZER
      map(() => true),
      catchError(() => of(false))
    );
  }



}
