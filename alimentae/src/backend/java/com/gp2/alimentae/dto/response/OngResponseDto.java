package com.gp2.alimentae.dto.response;

public record OngResponseDto(
        Long id,
        String nome,
        String cnpj,
        String endereco,
        String areaAtuacao,
        String telefone,
        Long usuarioId
) {
}

