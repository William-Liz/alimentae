
export interface EmpresaRequest {
  nomeFantasia: string;
  cnpj: string;
  endereco: string;
  telefone: string;
}

export interface EmpresaResponse {
  id: number;
  nomeFantasia: string;
  cnpj: string;
  endereco: string;
  telefone: string;
  usuarioId: number;
}
