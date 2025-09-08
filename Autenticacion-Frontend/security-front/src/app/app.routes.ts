// src/app/app.routes.ts

import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/MainLayout/MainLayout.component';
import { LoginComponent } from './features/login/Login/Login.component';
import { ProductsComponent } from './features/products/Products/Products.component';


export const routes: Routes = [
  // Definimos una ruta "padre" que englobará a casi toda nuestra aplicación.
  {
    path: '',
    component: MainLayoutComponent, // El componente principal para este grupo de rutas es nuestro Layout.
    children: [
      // --- Rutas Públicas ---
      {
        path: 'login', // Corresponde a la URL: /login
        component: LoginComponent,
        title: 'Iniciar Sesión' // Esto cambiará el título de la pestaña del navegador
      },

      // --- Rutas que serán Privadas en el futuro ---
      {
        path: 'products', // Corresponde a la URL: /products
        component: ProductsComponent,
        title: 'Productos'
      },

      // --- Ruta por Defecto ---
      {
        path: '', // Corresponde a la URL raíz (ej: http://localhost:4200)
        redirectTo: '/products', // Redirige automáticamente a la página de productos.
        pathMatch: 'full' // La redirección solo se aplica si la ruta está completamente vacía.
      }
    ]
  },

  // Aquí podríamos añadir rutas que NO usen el MainLayout, como una página 404.
  // { path: '**', component: NotFoundComponent }
];
