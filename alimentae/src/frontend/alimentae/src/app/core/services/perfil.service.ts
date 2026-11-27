
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { UsuarioResponse } from '../auth/auth.models';

@Injectable({
  providedIn: 'root',
})
export class PerfilService {
  private readonly http = inject(HttpClient);

  buscarPerfil(): Observable<UsuarioResponse> {
    return this.http.get<UsuarioResponse>('/api/usuarios/me');
  }
}
