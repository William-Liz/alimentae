
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  EmpresaRequest,
  EmpresaResponse,
} from './empresa.models';

@Injectable({
  providedIn: 'root',
})
export class EmpresaService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = '/api/empresas';

  // Busca a empresa vinculada ao usuário autenticado.
  buscarMinhaEmpresa(): Observable<EmpresaResponse> {
    return this.http.get<EmpresaResponse>(
      `${this.apiUrl}/me`,
    );
  }

  // Cadastra uma nova empresa.
  cadastrar(
    dados: EmpresaRequest,
  ): Observable<EmpresaResponse> {
    return this.http.post<EmpresaResponse>(
      this.apiUrl,
      dados,
    );
  }

  // Atualiza a empresa do usuário autenticado.
  atualizar(
    dados: EmpresaRequest,
  ): Observable<EmpresaResponse> {
    return this.http.put<EmpresaResponse>(
      `${this.apiUrl}/me`,
      dados,
    );
  }

  // Exclui a empresa do usuário autenticado.
  excluir(): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/me`,
    );
  }
}
