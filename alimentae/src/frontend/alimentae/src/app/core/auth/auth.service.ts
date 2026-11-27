
import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import {
  AuthSession,
  CadastroRequest,
  LoginRequest,
  LoginResponse,
  UsuarioResponse,
  TipoUsuario,
} from './auth.models';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly tokenKey = 'alimentae.token';
  private readonly tipoUsuarioKey = 'alimentae.tipoUsuario';
  private readonly expiresAtKey = 'alimentae.expiresAt';

  private readonly sessaoInicial = this.recuperarSessao();

  readonly token = signal<string | null>(
    this.sessaoInicial?.token ?? null,
  );

  readonly tipoUsuario = signal<TipoUsuario | null>(
    this.sessaoInicial?.tipoUsuario ?? null,
  );

  readonly expiresAt = signal<number | null>(
    this.sessaoInicial?.expiresAt ?? null,
  );

  readonly autenticado = computed(
    () => this.token() !== null && this.expiresAt() !== null,
  );

  /**
   * Realiza a autenticação no backend.
   */
  login(dados: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>('/api/auth/login', dados)
      .pipe(
        tap((resposta) => this.salvarSessao(resposta)),
      );
  }

  /**
   * Registra um novo usuário no backend.
   */
  cadastrar(
    dados: CadastroRequest,
  ): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(
      '/api/usuarios',
      dados,
    );
  }

  /**
   * Verifica se existe uma sessão válida.
   */
  isAuthenticated(): boolean {
    const tokenAtual = this.token();
    const expiracao = this.expiresAt();

    if (
      !tokenAtual ||
      !expiracao ||
      Date.now() >= expiracao
    ) {
      this.logout();
      return false;
    }

    return true;
  }

  /**
   * Encerra a sessão do usuário.
   */
  logout(): void {
    if (typeof window !== 'undefined') {
      sessionStorage.removeItem(this.tokenKey);
      sessionStorage.removeItem(this.tipoUsuarioKey);
      sessionStorage.removeItem(this.expiresAtKey);
    }

    this.token.set(null);
    this.tipoUsuario.set(null);
    this.expiresAt.set(null);
  }

  /**
   * Armazena os dados recebidos do login.
   */
  private salvarSessao(resposta: LoginResponse): void {
    const expiracao = Date.now() + resposta.expiresIn * 1000;

    const sessao: AuthSession = {
      token: resposta.token,
      tipoUsuario: resposta.tipoUsuario,
      expiresAt: expiracao,
    };

    sessionStorage.setItem(this.tokenKey, sessao.token);
    sessionStorage.setItem(
      this.tipoUsuarioKey,
      sessao.tipoUsuario,
    );
    sessionStorage.setItem(
      this.expiresAtKey,
      String(sessao.expiresAt),
    );

    this.token.set(sessao.token);
    this.tipoUsuario.set(sessao.tipoUsuario);
    this.expiresAt.set(sessao.expiresAt);
  }

  /**
   * Recupera uma sessão existente ao abrir a aplicação.
   */
  private recuperarSessao(): AuthSession | null {
    if (typeof window === 'undefined') {
      return null;
    }

    const token = sessionStorage.getItem(this.tokenKey);
    const tipoUsuario = sessionStorage.getItem(this.tipoUsuarioKey);
    const expiracao = Number(
      sessionStorage.getItem(this.expiresAtKey),
    );

    const tipoValido =
      tipoUsuario === 'EMPRESA' || tipoUsuario === 'ONG';

    if (
      !token ||
      !tipoValido ||
      !expiracao ||
      Date.now() >= expiracao
    ) {
      this.limparArmazenamento();
      return null;
    }

    return {
      token,
      tipoUsuario,
      expiresAt: expiracao,
    };
  }

  private limparArmazenamento(): void {
    if (typeof window === 'undefined') {
      return;
    }

    sessionStorage.removeItem(this.tokenKey);
    sessionStorage.removeItem(this.tipoUsuarioKey);
    sessionStorage.removeItem(this.expiresAtKey);
  }
}
