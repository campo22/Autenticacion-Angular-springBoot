import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/MainLayout/MainLayout.component';
import { LoginComponent } from './features/login/Login/Login.component';
import { ProductsComponent } from './features/products/Products/Products.component';
import { ProductFormComponent } from './features/products/product-form/product-form.component';

import { authGuard } from './core/guards/auth.guard';
import { loginGuard } from './core/guards/login.guard';
import { roleGuard } from './core/guards/role.guard'; // <-- ¡Asegúrate de importar el roleGuard!

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: 'login',
        component: LoginComponent,
        canActivate: [loginGuard]
      },
      {
        path: 'products',
        component: ProductsComponent,
        canActivate: [authGuard]
      },
      {
        path: 'products/new',
        component: ProductFormComponent,
        // ¡CORRECCIÓN! Encadenamos los guardianes: primero autenticación, luego rol.
        canActivate: [authGuard, roleGuard],
        data: {

          expectedRoles: ['ROLE_SUPERVISOR', 'ROLE_ADMIN']
        }
      },
      {
        path: 'products/edit/:id',
        component: ProductFormComponent,
        canActivate: [authGuard, roleGuard],
        data: {
          expectedRoles: ['ROLE_SUPERVISOR', 'ROLE_ADMIN']
        }
      },
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
