
package com.gp2.alimentae.controller;

import com.gp2.alimentae.dto.request.EmpresaRequestDto;
import com.gp2.alimentae.dto.response.EmpresaResponseDto;
import com.gp2.alimentae.service.EmpresaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    // Cadastrar empresa
    @PostMapping
    public ResponseEntity<EmpresaResponseDto> cadastrar(
            @Valid @RequestBody EmpresaRequestDto request,
            Authentication authentication) {

        EmpresaResponseDto empresa = empresaService.cadastrar(
                request,
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(empresa);
    }

    // Consultar empresa autenticada
    @GetMapping("/me")
    public ResponseEntity<EmpresaResponseDto> buscarMinhaEmpresa(
            Authentication authentication) {

        EmpresaResponseDto empresa = empresaService.buscarMinhaEmpresa(
                authentication.getName()
        );

        return ResponseEntity.ok(empresa);
    }

    // Atualizar empresa autenticada
    @PutMapping("/me")
    public ResponseEntity<EmpresaResponseDto> atualizar(
            @Valid @RequestBody EmpresaRequestDto request,
            Authentication authentication) {

        EmpresaResponseDto empresa = empresaService.atualizarMinhaEmpresa(
                request,
                authentication.getName()
        );

        return ResponseEntity.ok(empresa);
    }

    // Excluir empresa autenticada
    @DeleteMapping("/me")
    public ResponseEntity<Void> deletar(
            Authentication authentication) {

        empresaService.deletarMinhaEmpresa(
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}
