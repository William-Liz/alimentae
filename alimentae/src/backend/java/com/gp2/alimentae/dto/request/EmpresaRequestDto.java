
package com.gp2.alimentae.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmpresaRequestDto(

        @NotBlank(message = "O nome fantasia é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nomeFantasia,

        @NotBlank(message = "O CNPJ é obrigatório.")
        @Size(max = 18, message = "O CNPJ informado é inválido.")
        String cnpj,

        @NotBlank(message = "O endereço é obrigatório.")
        @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres.")
        String endereco,

        @NotBlank(message = "O telefone é obrigatório.")
        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
        String telefone

) {}
