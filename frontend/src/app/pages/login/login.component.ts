import { Component, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  imports: [MatButtonModule, MatCardModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  readonly error = signal('');

  constructor(private readonly auth: AuthService) {}

  async login(): Promise<void> {
    this.error.set('');
    try {
      await this.auth.signInWithGoogle();
    } catch (err) {
      this.error.set(err instanceof Error ? err.message : 'Não foi possível entrar com o Google.');
    }
  }
}
