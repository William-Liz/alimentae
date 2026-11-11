package com.gp2.alimentae.dto.response;

import com.gp2.alimentae.entity.StatusSolicitacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SolicitacaoResponseDto(
        Long id,
        BigDecimal quantidadeSolicitada,
        LocalDateTime dataSolicitacao,
        StatusSolicitacao status,
        Long ongId,
        Long doacaoId
) {
}
