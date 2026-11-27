
import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router,
} from '@angular/router';

import { AuthService } from './auth.service';

export const ongGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  if (authService.tipoUsuario() !== 'ONG') {
    return router.createUrlTree(['/perfil']);
  }

  return true;
};
