package com.gp2.alimentae.dto.request;

public record OngRequestDto(
        String nome,
        String cnpj,
        String endereco,
        String areaAtuacao,
        String telefone,
        Long usuarioId
) {
}

