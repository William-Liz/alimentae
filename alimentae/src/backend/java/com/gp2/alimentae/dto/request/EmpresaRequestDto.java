
package com.gp2.alimentae.dto.request;

public record EmpresaRequestDto(
        String nomeFantasia,
        String cnpj,
        String endereco,
        String telefone,
        Long usuarioId
) {
}

