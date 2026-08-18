import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { createClient, Session, SupabaseClient } from '@supabase/supabase-js';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly client: SupabaseClient;
  readonly session = signal<Session | null>(null);
  readonly ready = signal(false);

  constructor(private readonly router: Router) {
    this.client = createClient(environment.supabaseUrl, environment.supabaseAnonKey, {
      auth: {
        persistSession: true,
        detectSessionInUrl: true,
        flowType: 'pkce'
      }
    });
    void this.bootstrap();
  }

  private async bootstrap(): Promise<void> {
    const { data } = await this.client.auth.getSession();
    this.session.set(data.session);
    this.client.auth.onAuthStateChange((_event, session) => {
      this.session.set(session);
    });
    this.ready.set(true);
  }

  async signInWithGoogle(): Promise<void> {
    const { error } = await this.client.auth.signInWithOAuth({
      provider: 'google',
      options: {
        redirectTo: window.location.origin
      }
    });
    if (error) {
      throw error;
    }
  }

  async signOut(): Promise<void> {
    await this.client.auth.signOut();
    this.session.set(null);
    await this.router.navigateByUrl('/login');
  }

  async getAccessToken(): Promise<string | null> {
    const { data } = await this.client.auth.getSession();
    return data.session?.access_token ?? null;
  }

  displayName(): string {
    const current = this.session();
    if (!current) {
      return '';
    }
    const metadata = current.user.user_metadata as { full_name?: string; name?: string };
    return metadata.full_name || metadata.name || current.user.email || 'Usuário';
  }
}
