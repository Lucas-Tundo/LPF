import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = async () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const started = Date.now();
  while (!auth.ready() && Date.now() - started < 4000) {
    await new Promise((resolve) => setTimeout(resolve, 50));
  }
  if (auth.session()) {
    return true;
  }
  return router.createUrlTree(['/login']);
};
