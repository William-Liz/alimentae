
import { Component, inject, signal } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-cadastro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cadastro.html',
  styleUrl: './cadastro.css',
})
export class Cadastro {
  private readonly formBuilder = inject(FormBuilder);

  protected readonly senhaVisivel = signal(false);
  protected readonly mensagem = signal('');
  protected readonly tipoMensagem = signal<'success' | 'error' | ''>('');

  protected readonly formulario = this.formBuilder.nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    tipoUsuario: ['', Validators.required],
    senha: ['', [Validators.required, Validators.minLength(8)]],
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

    this.mensagem.set(
      'Formulário validado! O envio ao backend será implementado na próxima etapa.',
    );

    this.tipoMensagem.set('success');
  }
}
