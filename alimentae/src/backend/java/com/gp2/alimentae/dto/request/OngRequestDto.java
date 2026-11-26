package com.gp2.alimentae.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OngRequestDto(

        @NotBlank(message = "O nome da ONG é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nome,

        @NotBlank(message = "O CNPJ é obrigatório.")
        @Pattern(
                regexp = "\\d{14}",
                message = "O CNPJ deve conter exatamente 14 números."
        )
        String cnpj,

        @NotBlank(message = "O endereço é obrigatório.")
        @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres.")
        String endereco,

        @NotBlank(message = "A área de atuação é obrigatória.")
        @Size(max = 150, message = "A área de atuação deve ter no máximo 150 caracteres.")
        String areaAtuacao,

        @NotBlank(message = "O telefone é obrigatório.")
        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
        String telefone

) {}