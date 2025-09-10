import { APP_INITIALIZER, ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideClientHydration } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http'; // <-- ¡IMPORTs ACTUALIZADOS!
import { lastValueFrom } from 'rxjs';

import { routes } from './app.routes';
import { AuthService } from './core/services/auth.service';
import { authInterceptor } from './core/interceptors/auth.interceptor';
// <-- ¡IMPORTA TU INTERCEPTOR!

/**
 * Función factory para el inicializador de la aplicación.
 * Se ejecuta antes de que la aplicación sea visible, intentando restaurar la sesión
 * del usuario a través de un refresh token.
 *
 * @param authService El servicio de autenticación.
 * @returns una función que devuelve una Promesa que se resuelve cuando el intento de refresh ha finalizado.
 */
export function initializeAppFactory(authService: AuthService): () => Promise<any> {
  return () => lastValueFrom(authService.trySilentRefresh());
}

export const appConfig: ApplicationConfig = {
  providers: [
    // Configuración estándar de Angular
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideClientHydration(),

    // --- CONFIGURACIÓN DE HTTPCLIENT MEJORADA ---
    provideHttpClient(
      // 1. Registra nuestro interceptor para que añada la cabecera 'Authorization'
      withInterceptors([authInterceptor]),

    ),

    // --- INICIALIZADOR DE LA APLICACIÓN ---
    {
      provide: APP_INITIALIZER,
      useFactory: initializeAppFactory,
      deps: [AuthService],
      multi: true
    }
  ]
};
