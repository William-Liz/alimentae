
package com.gp2.alimentae.dto.response;

import com.gp2.alimentae.entity.TipoUsuario;

public record UsuarioResponseDto(
        Long id,
        String nome,
        String email,
        TipoUsuario tipoUsuario
) {
}

