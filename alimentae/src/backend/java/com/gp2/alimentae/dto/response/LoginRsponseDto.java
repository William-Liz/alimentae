package com.gp2.alimentae.dto.response;

public record LoginRsponseDto(String token, String tipoUsuario, long expiresIn) {
}
