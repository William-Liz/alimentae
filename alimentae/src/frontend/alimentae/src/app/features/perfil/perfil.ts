
import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';
import { UsuarioResponse } from '../../core/auth/auth.models';
import { PerfilService } from '../../core/services/perfil.service';

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil implements OnInit {
  private readonly perfilService = inject(PerfilService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly usuario = signal<UsuarioResponse | null>(null);
  protected readonly carregando = signal(true);
  protected readonly mensagemErro = signal('');

  ngOnInit(): void {
    this.carregarPerfil();
  }

  protected carregarPerfil(): void {
    this.carregando.set(true);
    this.mensagemErro.set('');

    this.perfilService.buscarPerfil().subscribe({
      next: (usuario) => {
        this.usuario.set(usuario);
        this.carregando.set(false);
      },

      error: (erro: HttpErrorResponse) => {
        this.carregando.set(false);

        if (erro.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login']);
          return;
        }

        if (erro.status === 404) {
          this.mensagemErro.set(
            'O endpoint do perfil não foi encontrado. Verifique a implementação de GET /api/usuarios/me no backend.',
          );
          return;
        }

        this.mensagemErro.set(
          'Não foi possível carregar seus dados. Tente novamente.',
        );
      },
    });
  }

  protected obterTipoUsuario(tipo: string): string {
    return tipo === 'EMPRESA'
      ? 'Empresa'
      : 'Organização não governamental (ONG)';
  }

  protected sair(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
