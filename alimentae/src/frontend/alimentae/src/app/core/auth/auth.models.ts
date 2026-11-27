
export type TipoUsuario = 'EMPRESA' | 'ONG';

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  token: string;
  tipoUsuario: TipoUsuario;
  expiresIn: number;
}

export interface CadastroRequest {
  nome: string;
  email: string;
  senha: string;
  tipoUsuario: TipoUsuario;
}

export interface UsuarioResponse {
  id: number;
  nome: string;
  email: string;
  tipoUsuario: TipoUsuario;
}

export interface AuthSession {
  token: string;
  tipoUsuario: TipoUsuario;
  expiresAt: number;
}
