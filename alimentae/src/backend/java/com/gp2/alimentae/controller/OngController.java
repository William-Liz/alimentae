package com.gp2.alimentae.controller;

import com.gp2.alimentae.dto.request.OngRequestDto;
import com.gp2.alimentae.dto.response.OngResponseDto;
import com.gp2.alimentae.entity.Ong;
import com.gp2.alimentae.entity.Usuario;
import com.gp2.alimentae.service.OngService;
import com.gp2.alimentae.dto.request.OngAtualizacaoRequestDto;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ongs")
public class OngController {

    private final OngService ongService;

    public OngController(OngService ongService) {
        this.ongService = ongService;
    }

    // Cadastro
    @PostMapping
    public ResponseEntity<OngResponseDto> cadastrar(
            @Valid @RequestBody OngRequestDto request,
            Authentication authentication) {

        Ong ong = new Ong();

        ong.setNome(request.nome());
        ong.setCnpj(request.cnpj());
        ong.setEndereco(request.endereco());
        ong.setAreaAtuacao(request.areaAtuacao());
        ong.setTelefone(request.telefone());

        Ong salva = ongService.cadastrar(
                ong,
                authentication.getName()
        );

        OngResponseDto response = paraDTO(salva);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Listar ONGs
    @GetMapping
    public ResponseEntity<List<OngResponseDto>> listar() {

        List<OngResponseDto> ongs = ongService.listar()
                .stream()
                .map(this::paraDTO)
                .toList();

        return ResponseEntity.ok(ongs);
    }

    // Buscar ONG por ID
    @GetMapping("/{id}")
    public ResponseEntity<OngResponseDto> buscarPorId(
            @PathVariable Long id) {

        Ong ong = ongService.buscarPorId(id);

        return ResponseEntity.ok(paraDTO(ong));
    }

    // Conversão para DTO de resposta
    private OngResponseDto paraDTO(Ong ong) {

        return new OngResponseDto(
                ong.getId(),
                ong.getNome(),
                ong.getCnpj(),
                ong.getEndereco(),
                ong.getAreaAtuacao(),
                ong.getTelefone(),
                ong.getUsuario().getId()
        );
    }

    // Atualizar dados da ONG
    @PutMapping("/{id}")
    public ResponseEntity<OngResponseDto> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody OngAtualizacaoRequestDto request,
            Authentication authentication) {

        Ong dadosAtualizados = new Ong();

        dadosAtualizados.setNome(request.nome());
        dadosAtualizados.setCnpj(request.cnpj());
        dadosAtualizados.setEndereco(request.endereco());
        dadosAtualizados.setAreaAtuacao(request.areaAtuacao());
        dadosAtualizados.setTelefone(request.telefone());

        Ong ongAtualizada = ongService.atualizar(
                id,
                dadosAtualizados,
                authentication.getName()
        );

        return ResponseEntity.ok(paraDTO(ongAtualizada));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> tratarNaoEncontrada(
            EntityNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarDadosInvalidos(
            IllegalArgumentException exception) {

        return ResponseEntity.badRequest()
                .body(Map.of("erro", exception.getMessage()));
    }
}