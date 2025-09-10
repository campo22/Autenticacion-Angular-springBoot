import { HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from "../services/auth.service";
import { inject } from "@angular/core";

/**
 * El HttpInterceptorFn es una función que se utiliza para interceptar las peticiones HTTP
 * y agregar el token de autenticación a las solicitudes que se realizen en la aplicación.
 *
 * Función que se encarga de agregar el token de autenticación
 * a las solicitudes HTTP que se realizen en la aplicación.
 * @param rep petición HTTP que se va a realizar
 * @param next función que se encarga de realizar la petición
 * @returns la petición modificada para que incluya el token de autenticación
 */
export const authInterceptor: HttpInterceptorFn = (rep, next) => {

  const authSerive = inject(AuthService);
  const token = authSerive.getAccessToken;

  const cloned = rep.clone({
    withCredentials: true, // Incluye las cookies en la petición
    setHeaders: token ? { Authorization: `Bearer ${token}` } : {} // Agrega el token de autenticación si existe
  });

  // Devuelve la petición modificada para que incluya el token de autenticación
  // y se realice la petición HTTP con la petición modificada y luego se pasa al siguiente interceptor
  return next(cloned);

};

