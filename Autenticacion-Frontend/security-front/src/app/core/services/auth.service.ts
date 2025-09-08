import { computed, Injectable, signal } from '@angular/core';
import axios from 'axios';
import { UserDto } from '../models/user.model';

const apiClient = axios.create({
  baseURL: 'http://localhost:5454/api',
  withCredentials: true
});

// el Injectable es un decorador que nos permite inyectar el servicio en cualquier lugar de la aplicacion
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  // # numereral es para indicar que es una variable privada detal modo que no se puede acceder desde afuera de la clase
  #currenUser = signal<UserDto | null | undefined>(undefined);


  isAuthenticated = computed(() => !!this.#currenUser());

  currentUser = computed(() => this.#currenUser());



}
