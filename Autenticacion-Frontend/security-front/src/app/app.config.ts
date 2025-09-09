import { APP_INITIALIZER, ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { AuthService } from './core/services/auth.service';
import { last, lastValueFrom } from 'rxjs';



/**
 * Factor a de inicializaci n de la aplicaci n que intenta refrescar la sesi n actual en segundo plano.
 *
 * Devuelve una funci n que se encarga de llamar al m todo {@link AuthService#trySilentRefresh} y devuelve su observable.
 *
 * Se utiliza en el APP_INITIALIZER para intentar refrescar la sesi n actual antes de que se inicie la aplicaci n.
 *
 * @param authService El servicio de autenticaci n que se utilizar  para intentar refrescar la sesi n actual.
 * @returns una funci n que devuelve un observable que se completa cuando se ha ejecutado el intento de refresco de la sesi n.
 */
export function initializeAppFactory(authService: AuthService): () => Promise<any> {
  return () => lastValueFrom(authService.trySilentRefresh());
}


export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
    provideClientHydration(),

    {
      provide: APP_INITIALIZER,
      useFactory: initializeAppFactory,
      deps: [AuthService],
      multi: true
    }
  ]
};
