package com.gp2.alimentae.dto.request;

import java.math.BigDecimal;

public record SolicitacaoRequestDto(
        Long ongId,
        Long doacaoId,
        BigDecimal quantidadeSolicitada
) {
}