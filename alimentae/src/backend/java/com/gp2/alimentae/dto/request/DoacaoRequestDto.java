package com.gp2.alimentae.dto.request;

import com.gp2.alimentae.entity.UnidadeMedida;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DoacaoRequestDto(
        String nomeAlimento,
        String descricao,
        BigDecimal quantidade,
        UnidadeMedida unidadeMedida,
        LocalDate validade,
        Long empresaId
) {
}
