import { inject } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";
import { map, take } from "rxjs";



export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);


  // 1. Obtenemos los roles esperados desde la configuración de la ruta.
  // 'route.data' es donde podemos pasar datos personalizados a nuestro guardián.
  // ejemplo: route.data['expectedRole'] = ['admin', 'user']
  const expectedRole: string[] = route.data['expectedRole'];



  if (!expectedRole || expectedRole.length === 0) {
    // Si no se han especificado roles en la ruta, redirigimos al usuario a la p gina de login.
    console.error('No se han especificado roles en la ruta');
    return router.createUrlTree(['/products']);
  }

  return authService.currentUser$.pipe(
    // take(1) para asegurarnos de que solo se ejecute el guardián una vez
    take(1),
    map(user => {

      if (!user || !user.roles) {
        // Si el usuario no está autenticado o no tiene roles, redirigimos al usuario a la p gina de login.
        console.error('El usuario no est  autenticado o no tiene roles');
        return router.createUrlTree(['/login']);
      }

      // Comprobamos si el usuario tiene al menos uno de los roles esperados
      const hasExpectedRole = user.roles.some(role => expectedRole.includes(role));

      if (hasExpectedRole) {
        return true;
      }
      console.warn('No tienes permiso para acceder a esta p gina');
      return router.createUrlTree(['/products']);
    })

  )

}

