import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  template: `
    <div class="login-wrapper">
      <div class="login-card">
        <h1>MultiConn Hub</h1>

        <button class="google-btn" (click)="onLoginclick()">Sign in with Google</button>
      </div>
    </div>
  `,
  styleUrl: './login.component.css',
})
export class LoginComponent {
  constructor(private authService: AuthService) {}

  public onLoginclick(): void {
    this.authService.loginWithGoogle();
  }
}
