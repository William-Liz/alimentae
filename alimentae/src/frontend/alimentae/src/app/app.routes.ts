
import { Routes } from '@angular/router';

import { Login } from './features/auth/login/login';
import { Cadastro } from './features/auth/cadastro/cadastro';
import { Perfil } from './features/perfil/perfil';

import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    component: Login,
    title: 'Login | Alimentaê',
  },
  {
    path: 'cadastro',
    component: Cadastro,
    title: 'Cadastro | Alimentaê',
  },
  {
    path: 'perfil',
    component: Perfil,
    canActivate: [authGuard],
    title: 'Meu perfil | Alimentaê',
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
