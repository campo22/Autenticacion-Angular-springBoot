import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/MainLayout/MainLayout.component';
import { LoginComponent } from './features/login/Login/Login.component';
import { ProductsComponent } from './features/products/Products/Products.component';


import { authGuard } from './core/guards/auth.guard';
import { loginGuard } from './core/guards/login.guard';
import { roleGuard } from './core/guards/role.guard'; // ¡Importamos el nuevo guardián!

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      // --- Rutas Públicas ---
      {
        path: 'login',
        component: LoginComponent,
        canActivate: [loginGuard]
      },

      // --- Rutas para Usuarios Autenticados ---
      {
        path: 'products',
        component: ProductsComponent,
        canActivate: [authGuard]
      },

      // // --- Rutas Protegidas por Rol ---
      // {
      //   path: 'supervisor-dashboard',

      //   canActivate: [authGuard, roleGuard], // ¡Encadenamos los guardianes!
      //   data: {
      //     // Le pasamos los roles permitidos al roleGuard
      //     expectedRoles: ['ROLE_ADMIN']
      //   }
      // },
      // {
      //   path: 'admin-panel',

      //   canActivate: [authGuard, roleGuard],
      //   data: {
      //     // Solo los ADMIN pueden acceder aquí
      //     expectedRoles: ['ROLE_ADMIN']
      //   }
      // },

      // --- Redirecciones ---
      {
        path: '',
        redirectTo: 'products',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
