import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';
  loading = false;

  constructor(private apiService: ApiService, private router: Router) {}

  login() {
  if (!this.username || !this.password) {
    this.error = 'Ingresa usuario y contraseña.';
    return;
  }
  this.loading = true;
  this.error = '';
  this.apiService.login(this.username, this.password).subscribe({
    next: () => {
      this.apiService.saveCredentials(this.username, this.password);
      this.router.navigate(['/groups']);
    },
    error: () => {
      this.error = 'Credenciales incorrectas. Intenta de nuevo.';
      this.loading = false;
    }
  });
}
}