package com.gp2.alimentae.dto.response;

import com.gp2.alimentae.entity.StatusDoacao;
import com.gp2.alimentae.entity.UnidadeMedida;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DoacaoResponseDto(
        Long id,
        String nomeAlimento,
        String descricao,
        BigDecimal quantidade,
        BigDecimal quantidadeDisponivel,
        UnidadeMedida unidadeMedida,
        LocalDate validade,
        LocalDateTime dataCadastro,
        StatusDoacao status,
        Long empresaId
) {
}

