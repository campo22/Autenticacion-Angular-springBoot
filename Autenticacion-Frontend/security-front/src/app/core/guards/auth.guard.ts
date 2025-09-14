import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, take } from 'rxjs/operators';

/**
 * Guardián funcional para proteger rutas que requieren autenticación.
 *
 * Este guardián implementa la interfaz CanActivateFn. El enrutador de Angular
 * lo ejecutará antes de activar cualquier ruta a la que esté asignado.
 *
 * @returns {Observable<boolean | UrlTree>}
 *  - Un Observable que emite `true` si la navegación está permitida.
 *  - Un Observable que emite un `UrlTree` (una redirección) si la navegación es denegada.
 */
export const authGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);


  return authService.currentUser$.pipe(

    take(1),

    map(user => {
      const isAuthenticated = !!user;

      // Si el usuario est  autenticado, permitimos la navegación
      if (isAuthenticated) {
        return true;
      }

      return router.createUrlTree(['/login']);
    })
  );
};
