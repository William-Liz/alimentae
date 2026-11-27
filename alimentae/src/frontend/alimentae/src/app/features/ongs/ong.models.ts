
export interface OngRequest {
  nome: string;
  cnpj: string;
  endereco: string;
  areaAtuacao: string;
  telefone: string;
}

export interface OngResponse {
  id: number;
  nome: string;
  cnpj: string;
  endereco: string;
  areaAtuacao: string;
  telefone: string;
  usuarioId: number;
}
