
import { Component, inject, signal } from '@angular/core';

import { Router } from '@angular/router';

import { HttpErrorResponse } from '@angular/common/http';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { RouterLink } from '@angular/router';

import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly senhaVisivel = signal(false);
  protected readonly mensagem = signal('');
  protected readonly tipoMensagem = signal<'success' | 'error' | ''>('');
  protected readonly carregando = signal(false);

  protected readonly token = this.authService.token;
  protected readonly tipoUsuario = this.authService.tipoUsuario;

  protected readonly formulario = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required]],
  });

  protected alternarVisibilidadeSenha(): void {
    this.senhaVisivel.update((visivel) => !visivel);
  }

  protected entrar(): void {
    this.mensagem.set('');
    this.tipoMensagem.set('');

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.carregando.set(true);

    this.authService.login(this.formulario.getRawValue()).subscribe({
      next: () => {
  this.carregando.set(false);
  this.router.navigateByUrl('/perfil');
},

      error: (erro: HttpErrorResponse) => {
        this.carregando.set(false);

        this.mensagem.set(this.obterMensagemErro(erro));
        this.tipoMensagem.set('error');
      },
    });
  }

  protected sair(): void {
    this.authService.logout();

    this.mensagem.set('Sua sessão foi encerrada.');
    this.tipoMensagem.set('success');
  }

  private obterMensagemErro(erro: HttpErrorResponse): string {
    if (erro.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique se o backend está funcionando.';
    }

    if (erro.status === 401) {
      return 'E-mail ou senha inválidos.';
    }

    const mensagemBackend = erro.error?.message;

    if (typeof mensagemBackend === 'string') {
      return mensagemBackend;
    }

    return 'Ocorreu um erro durante a autenticação.';
  }
}
