
import { Component, inject, signal } from '@angular/core';

import { HttpErrorResponse } from '@angular/common/http';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { RouterLink } from '@angular/router';

import { AuthService } from '../../../core/auth/auth.service';
import { TipoUsuario } from '../../../core/auth/auth.models';

@Component({
  selector: 'app-cadastro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cadastro.html',
  styleUrl: './cadastro.css',
})
export class Cadastro {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);

  protected readonly senhaVisivel = signal(false);
  protected readonly mensagem = signal('');
  protected readonly tipoMensagem = signal<'success' | 'error' | ''>('');
  protected readonly carregando = signal(false);

  protected readonly formulario = this.formBuilder.nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    tipoUsuario: ['', Validators.required],
    senha: ['', Validators.required],
    confirmarSenha: ['', Validators.required],
  });

  protected alternarVisibilidadeSenha(): void {
    this.senhaVisivel.update((visivel) => !visivel);
  }

  protected cadastrar(): void {
    this.mensagem.set('');
    this.tipoMensagem.set('');

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const dados = this.formulario.getRawValue();

    if (dados.senha !== dados.confirmarSenha) {
      this.mensagem.set('As senhas informadas não coincidem.');
      this.tipoMensagem.set('error');
      return;
    }

    this.carregando.set(true);

    this.authService
      .cadastrar({
        nome: dados.nome.trim(),
        email: dados.email.trim().toLowerCase(),
        senha: dados.senha,
        tipoUsuario: dados.tipoUsuario as TipoUsuario,
      })
      .subscribe({
        next: (usuario) => {
          this.carregando.set(false);

          this.mensagem.set(
            `Cadastro de ${usuario.nome} realizado com sucesso! Agora você pode entrar.`,
          );

          this.tipoMensagem.set('success');
          this.formulario.reset();
        },

        error: (erro: HttpErrorResponse) => {
          this.carregando.set(false);

          this.mensagem.set(this.obterMensagemErro(erro));
          this.tipoMensagem.set('error');
        },
      });
  }

  private obterMensagemErro(erro: HttpErrorResponse): string {
    if (erro.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique se o backend está funcionando.';
    }

    const mensagemBackend = erro.error?.message;

    if (typeof mensagemBackend === 'string') {
      return mensagemBackend;
    }

    return 'Não foi possível realizar o cadastro.';
  }
}
