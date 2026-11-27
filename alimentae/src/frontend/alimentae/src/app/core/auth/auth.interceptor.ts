
import { inject } from '@angular/core';
import { HttpInterceptorFn } from '@angular/common/http';

import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (
  requisicao,
  next,
) => {
  const authService = inject(AuthService);

  const requisicaoLogin =
    requisicao.url.includes('/api/auth/login');

  const requisicaoCadastro =
    requisicao.method === 'POST' &&
    requisicao.url.includes('/api/usuarios');

  // Login e cadastro são endpoints públicos.
  if (requisicaoLogin || requisicaoCadastro) {
    return next(requisicao);
  }

  // Não envia token inexistente ou expirado.
  if (!authService.isAuthenticated()) {
    return next(requisicao);
  }

  const token = authService.token();

  if (!token) {
    return next(requisicao);
  }

  const requisicaoAutenticada = requisicao.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`,
    },
  });

  return next(requisicaoAutenticada);
};
