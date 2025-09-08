import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../Navbar/Navbar.component';
import { FooterComponent } from '../Footer/Footer.component';

@Component({
  selector: 'app-MainLayout',
  templateUrl: './MainLayout.component.html',
  styleUrls: ['./MainLayout.component.css'],
  standalone: true,
  imports: [NavbarComponent, FooterComponent, RouterModule]



})
export class MainLayoutComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
