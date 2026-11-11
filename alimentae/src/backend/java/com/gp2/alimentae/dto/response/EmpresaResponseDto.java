package com.gp2.alimentae.dto.response;

public record EmpresaResponseDto(
        Long id,
        String nomeFantasia,
        String cnpj,
        String endereco,
        String telefone,
        Long usuarioId
) {
}

