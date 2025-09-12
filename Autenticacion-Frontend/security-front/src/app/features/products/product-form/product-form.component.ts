import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProductService } from '../product.service.ts.service';

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

  private fb = inject(FormBuilder);
  private router = inject(Router);
  private productService = inject(ProductService);
  private route = inject(ActivatedRoute); // Para acceder a los parámetros de la URL
  private location = inject(Location); // Para navegar hacia la página anterior

  productForm!: FormGroup;
  isEditModel = false;
  productId: number | null = null;
  errorMessage?: string | null = null;




  constructor() { }

  ngOnInit() {
  }

}
