import { CommonModule, Location } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProductService } from '../product.service.ts.service';
import { of, switchMap } from 'rxjs';
import { ProductRequest } from '../../../shared/models/product.model';

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  standalone: true
})
export class ProductFormComponent implements OnInit {
  constructor() {

  }

  ngOnInit(): void {
    this.initForm();
    this.checkModel();
  }


  private fb = inject(FormBuilder);
  private router = inject(Router);
  private productService = inject(ProductService);
  private route = inject(ActivatedRoute); // Para acceder a los parámetros de la URL
  private location = inject(Location); // Para navegar hacia la página anterior

  productForm!: FormGroup;
  isEditMode = false;
  productId: number | null = null;
  error?: string | null = null;

  private initForm(): void {

    this.productForm = this.fb.group({
      name: ['', [Validators.minLength(3)]],
      description: ['', [Validators.minLength(3)]],
      price: [0, [Validators.required, Validators.min(0.01)]],

    });
  }

  private checkModel(): void {

    this.route.paramMap.pipe(

      // el switchMap significa que se suscribe a los cambios en los
      // parámetros de la URL
      switchMap(params => {

        const id = params.get('id');
        if (id) {
          this.isEditMode = true;
          this.productId = +id; // el + convierte el string en un number
          return this.productService.getProductById(this.productId);
        } else {
          this.isEditMode = false;
          return of(null); // Devolvemos un observable vacio
        }
      })

    ).subscribe(product => {

      if (product) {
        // si el producto existe, lo cargamos en el formulario
        this.productForm.patchValue(product)
      }
    })

  }
  onSubmit(): void {

    if (this.productForm.invalid) {
      return;
    }
    this.error = null;
    const productData: ProductRequest = this.productForm.value

    const savedProduct$ = this.isEditMode
      ? this.productService.updateProduct(this.productId!, productData)
      : this.productService.createProduct(productData);

    savedProduct$.subscribe({
      next: () => {
        this.router.navigate(['/products'])
      },
      error: (err) => {
        console.error('Error al guardar el producto', err);
        this.error = err.message;
      }

    });

  }
  goBack(): void {
    this.location.back();
  }


}
