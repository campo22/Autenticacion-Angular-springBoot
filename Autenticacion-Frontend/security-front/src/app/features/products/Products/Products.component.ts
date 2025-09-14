import { Component, inject, OnInit } from '@angular/core';
import { ProductService } from '../product.service.ts.service';
import { AuthService } from '../../../core/services/auth.service';
import { catchError, Observable, of } from 'rxjs';
import { Product } from '../../../shared/models/product.model';
import { AuthResponse } from '../../../shared/models/auth.model';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ConfirmationDialogComponent } from "../../../shared/components/confirmation-dialog/confirmation-dialog.component";


@Component({
  selector: 'app-Products',
  templateUrl: './Products.component.html',
  styleUrls: ['./Products.component.css'],
  standalone: true,
  imports: [
    CommonModule, // el common module es necesario para el ngIf, el ngFor,async pipe ,
    CurrencyPipe, // el currency pipe es necesario para formatear el precio
    RouterLink,
    ConfirmationDialogComponent
  ]
})
export class ProductsComponent implements OnInit {

  private productService = inject(ProductService);
  private authService = inject(AuthService);

  public products$!: Observable<Product[]>;
  public currentUser$: Observable<AuthResponse | null> = this.authService.currentUser$
  public error?: string | null = null;

  showDeleteConfirmation = false;
  productToDelete: Product | null = null;




  /**
   * Muestra el diálogo de confirmación para eliminar un producto.
   * Almacena el producto a eliminar en la propiedad "productToDelete" y muestra el diálogo de confirmación.
   * @param product El producto a eliminar.
   */
  onDelete(product: Product): void {
    this.productToDelete = product;
    this.showDeleteConfirmation = true;
  }

  /**
   * Cancela la eliminación de un producto.
   * Oculta el diálogo de confirmación y restablece el producto a eliminar.
   */
  onCancelDelete(): void {
    this.showDeleteConfirmation = false;
    this.productToDelete = null;
  }


  ngOnInit() {
    this.loadProducts();
  }

  /**
   * Carga los productos y gestiona errores.
   * Si ocurre un error al cargar los productos, se muestra un mensaje de error
   * y se devuelve un observable vacio.
   */
  loadProducts(): void {

    this.products$ = this.productService.getProducts().pipe(
      catchError(err => {
        console.error(' Error al cargar los productos', err);
        this.error = ' Ocurrio un error al cargar los productos';

        // Devolvemos un observable vacio
        return of([]);

      })
    );
  }
  onConfirmDelete(): void {
    if (!this.productToDelete) return;

    this.productService.deleteProduct(this.productToDelete.id).subscribe({
      next: () => {
        console.log(`Producto ${this.productToDelete?.name} eliminado exitosamente.`);

        this.showDeleteConfirmation = false;
        this.productToDelete = null;

        this.loadProducts();

      },
      error: (err) => {
        console.error('Error al eliminar el producto', err);
        this.error = 'Ocurrio un error al eliminar el producto';

        this.showDeleteConfirmation = false;
        this.productToDelete = null;
      }
    });

  }
  /**
   * Verifica si el usuario tiene alguno de los roles especificados.
   * @param user El usuario autenticado o null si no hay usuario autenticado.
   * @param roles Los roles que se van a verificar.
   * @returns true si el usuario tiene alguno de los roles, false en caso contrario.
   */
  userHasRole(user: AuthResponse | null, roles: string[]) {

    if (!user || !user.roles) {
      return false;
    }

    // Verificamos si el usuario tiene alguno de los roles especificados
    // some() devuelve true si al menos uno de los elementos cumple la condición
    return roles.some(role => user.roles.includes(role));
  }

}
