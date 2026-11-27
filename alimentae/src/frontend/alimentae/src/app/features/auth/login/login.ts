
import { Component, inject, signal } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly formBuilder = inject(FormBuilder);

  protected readonly senhaVisivel = signal(false);
  protected readonly mensagem = signal('');

  protected readonly formulario = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required]],
  });

  protected alternarVisibilidadeSenha(): void {
    this.senhaVisivel.update((visivel) => !visivel);
  }

  protected entrar(): void {
    this.mensagem.set('');

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.mensagem.set(
      'Formulário validado! A integração com o backend será implementada na próxima etapa.',
    );
  }
}
