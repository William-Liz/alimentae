
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

import { EmpresaService } from './empresa.service';

import {
  EmpresaRequest,
  EmpresaResponse,
} from './empresa.models';

// Valida se o CNPJ possui 14 números após remover a formatação.
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

type EstadoEmpresa =
  | 'carregando'
  | 'cadastro'
  | 'visualizacao'
  | 'edicao'
  | 'erro';

@Component({
  selector: 'app-empresas',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './empresas.html',
  styleUrl: './empresas.css',
})
export class Empresas implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly empresaService = inject(EmpresaService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly empresa = signal<EmpresaResponse | null>(null);

  protected readonly estado = signal<EstadoEmpresa>(
    'carregando',
  );

  protected readonly carregando = signal(false);
  protected readonly mensagemErro = signal('');
  protected readonly mensagemSucesso = signal('');

  protected readonly formulario = this.formBuilder.nonNullable.group({
    nomeFantasia: [
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

    telefone: [
      '',
      [
        Validators.required,
        Validators.maxLength(20),
      ],
    ],
  });

  ngOnInit(): void {
    this.carregarEmpresa();
  }

  // Busca a empresa do usuário autenticado.
  protected carregarEmpresa(): void {
    this.estado.set('carregando');
    this.mensagemErro.set('');
    this.mensagemSucesso.set('');

    this.empresaService.buscarMinhaEmpresa().subscribe({
      next: (empresa) => {
        this.empresa.set(empresa);
        this.preencherFormulario(empresa);
        this.estado.set('visualizacao');
      },

      error: (erro: HttpErrorResponse) => {
        if (erro.status === 404) {
          this.empresa.set(null);
          this.formulario.reset();
          this.estado.set('cadastro');
          return;
        }

        if (erro.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login']);
          return;
        }

        this.mensagemErro.set(
          this.obterMensagemErro(erro),
        );

        this.estado.set('erro');
      },
    });
  }

  // Preenche o formulário com os dados existentes.
  private preencherFormulario(
    empresa: EmpresaResponse,
  ): void {
    this.formulario.patchValue({
      nomeFantasia: empresa.nomeFantasia,
      cnpj: empresa.cnpj,
      endereco: empresa.endereco,
      telefone: empresa.telefone,
    });
  }

  // Abre o formulário de edição.
  protected editar(): void {
    const empresaAtual = this.empresa();

    if (!empresaAtual) {
      return;
    }

    this.preencherFormulario(empresaAtual);
    this.mensagemErro.set('');
    this.mensagemSucesso.set('');
    this.estado.set('edicao');
  }

  // Cancela a edição e recupera os dados originais.
  protected cancelar(): void {
    const empresaAtual = this.empresa();

    this.mensagemErro.set('');

    if (empresaAtual) {
      this.preencherFormulario(empresaAtual);
      this.estado.set('visualizacao');
      return;
    }

    this.formulario.reset();
    this.estado.set('cadastro');
  }

  // Cadastra ou atualiza a empresa.
  protected salvar(): void {
    this.mensagemErro.set('');
    this.mensagemSucesso.set('');

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const dadosFormulario = this.formulario.getRawValue();

    const dados: EmpresaRequest = {
      nomeFantasia: dadosFormulario.nomeFantasia.trim(),
      cnpj: dadosFormulario.cnpj.replace(/\D/g, ''),
      endereco: dadosFormulario.endereco.trim(),
      telefone: dadosFormulario.telefone.trim(),
    };

    const atualizando = this.empresa() !== null;

    this.carregando.set(true);

    const requisicao = atualizando
      ? this.empresaService.atualizar(dados)
      : this.empresaService.cadastrar(dados);

    requisicao.subscribe({
      next: (empresaSalva) => {
        this.empresa.set(empresaSalva);

        this.preencherFormulario(empresaSalva);

        this.carregando.set(false);
        this.estado.set('visualizacao');

        this.mensagemSucesso.set(
          atualizando
            ? 'Dados da empresa atualizados com sucesso!'
            : 'Empresa cadastrada com sucesso!',
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

  // Exclui a empresa após confirmação.
  protected excluir(): void {
    if (!this.empresa()) {
      return;
    }

    const confirmado = window.confirm(
      'Tem certeza de que deseja excluir o cadastro da empresa?',
    );

    if (!confirmado) {
      return;
    }

    this.carregando.set(true);
    this.mensagemErro.set('');
    this.mensagemSucesso.set('');

    this.empresaService.excluir().subscribe({
      next: () => {
        this.empresa.set(null);
        this.formulario.reset();

        this.carregando.set(false);
        this.estado.set('cadastro');

        this.mensagemSucesso.set(
          'Cadastro da empresa excluído com sucesso.',
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

  // Converte erros HTTP em mensagens para o usuário.
  private obterMensagemErro(
    erro: HttpErrorResponse,
  ): string {
    if (erro.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique se o backend está funcionando.';
    }

    const mensagemBackend = erro.error?.message;

    if (typeof mensagemBackend === 'string') {
      return mensagemBackend;
    }

    if (erro.status === 400) {
      return 'Confira os dados informados e tente novamente.';
    }

    if (erro.status === 403) {
      return 'Você não tem permissão para realizar esta operação.';
    }

    if (erro.status === 404) {
      return 'A empresa não foi encontrada.';
    }

    if (erro.status === 409) {
      return 'Este CNPJ já está cadastrado.';
    }

    return 'Ocorreu um erro inesperado. Tente novamente.';
  }
}
