
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  OngRequest,
  OngResponse,
} from './ong.models';

@Injectable({
  providedIn: 'root',
})
export class OngService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = '/ongs';

  // Busca os dados da ONG do usuário autenticado.
  buscarMinhaOng(): Observable<OngResponse> {
    return this.http.get<OngResponse>(
      `${this.apiUrl}/me`,
    );
  }

  // Cadastra uma nova ONG.
  cadastrar(dados: OngRequest): Observable<OngResponse> {
    return this.http.post<OngResponse>(
      this.apiUrl,
      dados,
    );
  }

  // Atualiza os dados da ONG autenticada.
  atualizar(dados: OngRequest): Observable<OngResponse> {
    return this.http.put<OngResponse>(
      `${this.apiUrl}/me`,
      dados,
    );
  }

  // Exclui a ONG autenticada.
  excluir(): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/me`,
    );
  }
}
