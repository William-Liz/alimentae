
package com.gp2.alimentae.dto.request;

import com.gp2.alimentae.entity.TipoUsuario;

public record UsuarioRequestDto(
        String nome,
        String email,
        String senha,
        TipoUsuario tipoUsuario
) {
}
