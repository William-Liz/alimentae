
import { Routes } from '@angular/router';

import { Login } from './features/auth/login/login';
import { Cadastro } from './features/auth/cadastro/cadastro';

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
    path: '**',
    redirectTo: 'login',
  },
];
