
import {
  Component,
  inject,
  OnInit,
  signal,
} from '@angular/core';

import { HttpErrorResponse } from '@angular/common/http';

import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';

import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';
import { OngService } from './ong.service';

import {
  OngRequest,
  OngResponse,
} from './ong.models';

// Valida o CNPJ considerando somente os números.
const validarCnpj: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null => {
  if (!control.value) {
    return null;
  }

  const cnpj = String(control.value).replace(/\D/g, '');

  return cnpj.length === 14
    ? null
    : { cnpjInvalido: true };
};

type EstadoOng =
  | 'carregando'
  | 'cadastro'
  | 'visualizacao'
  | 'edicao'
  | 'erro';

@Component({
  selector: 'app-ongs',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './ongs.html',
  styleUrl: './ongs.css',
})
export class Ongs implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly ongService = inject(OngService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly ong = signal<OngResponse | null>(null);

  protected readonly estado = signal<EstadoOng>(
    'carregando',
  );

  protected readonly carregando = signal(false);
  protected readonly mensagemErro = signal('');
  protected readonly mensagemSucesso = signal('');

  protected readonly formulario = this.formBuilder.nonNullable.group({
    nome: [
      '',
      [
        Validators.required,
        Validators.maxLength(150),
      ],
    ],

    cnpj: [
      '',
      [
        Validators.required,
        Validators.maxLength(18),
        validarCnpj,
      ],
    ],

    endereco: [
      '',
      [
        Validators.required,
        Validators.maxLength(255),
      ],
    ],

    areaAtuacao: [
      '',
      [
        Validators.required,
        Validators.maxLength(150),
      ],
    ],

    telefone: [
      '',
      [
        Validators.required,
        Validators.maxLength(20),
      ],
    ],
  });

  ngOnInit(): void {
    this.carregarOng();
  }

  // Busca a ONG do usuário autenticado.
  protected carregarOng(): void {
    this.estado.set('carregando');

    this.mensagemErro.set('');
    this.mensagemSucesso.set('');

    this.ongService.buscarMinhaOng().subscribe({
      next: (ong) => {
        this.ong.set(ong);
        this.preencherFormulario(ong);
        this.estado.set('visualizacao');
      },

      error: (erro: HttpErrorResponse) => {
        if (erro.status === 404) {
          this.ong.set(null);
          this.formulario.reset();
          this.estado.set('cadastro');
          return;
        }

        if (erro.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login']);
          return;
        }

        if (erro.status === 403) {
          this.mensagemErro.set(
            'Sua conta não possui permissão para acessar esta área.',
          );
          this.estado.set('erro');
          return;
        }

        this.mensagemErro.set(
          this.obterMensagemErro(erro),
        );

        this.estado.set('erro');
      },
    });
  }

  // Preenche os campos com os dados existentes.
  private preencherFormulario(ong: OngResponse): void {
    this.formulario.patchValue({
      nome: ong.nome,
      cnpj: ong.cnpj,
      endereco: ong.endereco,
      areaAtuacao: ong.areaAtuacao,
      telefone: ong.telefone,
    });
  }

  // Abre o formulário de edição.
  protected editar(): void {
    const ongAtual = this.ong();

    if (!ongAtual) {
      return;
    }

    this.preencherFormulario(ongAtual);

    this.mensagemErro.set('');
    this.mensagemSucesso.set('');

    this.estado.set('edicao');
  }

  // Cancela a edição.
  protected cancelar(): void {
    const ongAtual = this.ong();

    this.mensagemErro.set('');

    if (ongAtual) {
      this.preencherFormulario(ongAtual);
      this.estado.set('visualizacao');
      return;
    }

    this.formulario.reset();
    this.estado.set('cadastro');
  }

  // Cadastra ou atualiza uma ONG.
  protected salvar(): void {
    this.mensagemErro.set('');
    this.mensagemSucesso.set('');

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const valores = this.formulario.getRawValue();

    const dados: OngRequest = {
      nome: valores.nome.trim(),
      cnpj: valores.cnpj.replace(/\D/g, ''),
      endereco: valores.endereco.trim(),
      areaAtuacao: valores.areaAtuacao.trim(),
      telefone: valores.telefone.trim(),
    };

    const atualizando = this.ong() !== null;

    this.carregando.set(true);

    const requisicao = atualizando
      ? this.ongService.atualizar(dados)
      : this.ongService.cadastrar(dados);

    requisicao.subscribe({
      next: (ongSalva) => {
        this.ong.set(ongSalva);

        this.preencherFormulario(ongSalva);

        this.carregando.set(false);
        this.estado.set('visualizacao');

        this.mensagemSucesso.set(
          atualizando
            ? 'Dados da ONG atualizados com sucesso!'
            : 'ONG cadastrada com sucesso!',
        );
      },

      error: (erro: HttpErrorResponse) => {
        this.carregando.set(false);

        this.mensagemErro.set(
          this.obterMensagemErro(erro),
        );
      },
    });
  }

  // Exclui o cadastro após confirmação.
  protected excluir(): void {
    if (!this.ong()) {
      return;
    }

    const confirmado = window.confirm(
      'Tem certeza de que deseja excluir o cadastro da ONG?',
    );

    if (!confirmado) {
      return;
    }

    this.carregando.set(true);

    this.mensagemErro.set('');
    this.mensagemSucesso.set('');

    this.ongService.excluir().subscribe({
      next: () => {
        this.ong.set(null);
        this.formulario.reset();

        this.carregando.set(false);
        this.estado.set('cadastro');

        this.mensagemSucesso.set(
          'Cadastro da ONG excluído com sucesso.',
        );
      },

      error: (erro: HttpErrorResponse) => {
        this.carregando.set(false);

        this.mensagemErro.set(
          this.obterMensagemErro(erro),
        );
      },
    });
  }

  // Encerra a sessão.
  protected sair(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // Traduz os erros HTTP para mensagens compreensíveis.
  private obterMensagemErro(
    erro: HttpErrorResponse,
  ): string {
    if (erro.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique se o backend está funcionando.';
    }

    const mensagemBackend =
      erro.error?.message ?? erro.error?.erro;

    if (typeof mensagemBackend === 'string') {
      return mensagemBackend;
    }

    if (erro.status === 400) {
      return 'Confira os dados informados e tente novamente.';
    }

    if (erro.status === 403) {
      return 'Você não tem permissão para realizar esta operação.';
    }

    if (erro.status === 409) {
      return 'Este CNPJ já está cadastrado ou sua conta já possui um perfil institucional.';
    }

    return 'Ocorreu um erro inesperado. Tente novamente.';
  }
}
