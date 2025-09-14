import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getAccessToken;

  // 1. Clonamos la petición UNA SOLA VEZ para añadir withCredentials.
  // Esto asegura que TODAS las peticiones (login, refresh, products, etc.)
  // siempre estén habilitadas para manejar cookies.
  let clonedReq = req.clone({
    withCredentials: true,
  });

  // 2. Si hay un token, clonamos DE NUEVO para añadir la cabecera.
  // Esto solo se aplica a las peticiones que necesitan autenticación Bearer.
  if (token) {
    clonedReq = clonedReq.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  // 3. Pasamos la petición final.
  return next(clonedReq);
};
