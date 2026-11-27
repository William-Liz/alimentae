
import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router,
} from '@angular/router';

import { AuthService } from './auth.service';

export const empresaGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  if (authService.tipoUsuario() !== 'EMPRESA') {
    return router.createUrlTree(['/perfil']);
  }

  return true;
};
