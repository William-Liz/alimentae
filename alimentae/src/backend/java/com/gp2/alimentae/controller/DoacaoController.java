package com.gp2.alimentae.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gp2.alimentae.dto.request.DoacaoRequestDto;
import com.gp2.alimentae.dto.request.EmpresaRequestDto;
import com.gp2.alimentae.dto.response.DoacaoResponseDto;
import com.gp2.alimentae.entity.Doacao;
import com.gp2.alimentae.service.DoacaoService;

@RestController
@RequestMapping("Api/doacao")
public class DoacaoController {

	private final DoacaoService doacaoService;

	public DoacaoController(DoacaoService doacaoService) {
		this.doacaoService = doacaoService;
	}
	// Cadastrar
	@PostMapping
	public ResponseEntity<DoacaoResponseDto> cadastro(@RequestBody DoacaoRequestDto request) {
		
		Doacao doacao = new Doacao();
		
        Doacao salva = doacaoService.cadastrar(doacao);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(converterParaDto(salva));
    }

    // Listar todas as doações
    @GetMapping
    public ResponseEntity<List<DoacaoResponseDto>> listar() {

        List<DoacaoResponseDto> response = doacaoService.listar()
                .stream()
                .map(this::converterParaDto)
                .toList();

        return ResponseEntity.ok(response);
    }

    // Listar doações disponíveis
    @GetMapping("/disponiveis")
    public ResponseEntity<List<DoacaoResponseDto>> listarDisponiveis() {

        List<DoacaoResponseDto> response = doacaoService.listarDisponiveis()
                .stream()
                .map(this::converterParaDto)
                .toList();

        return ResponseEntity.ok(response);
    }

    // Buscar doação por ID
    @GetMapping("/{id}")
    public ResponseEntity<DoacaoResponseDto> buscarPorId(
            @PathVariable Long id) {

        Doacao doacao = doacaoService.buscarPorId(id);

        return ResponseEntity.ok(converterParaDto(doacao));
    }

    // Listar doações de uma empresa
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<DoacaoResponseDto>> listarPorEmpresa(
            @PathVariable Long empresaId) {

        List<DoacaoResponseDto> response =
                doacaoService.listarPorEmpresa(empresaId)
                        .stream()
                        .map(this::converterParaDto)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // Cancelar doação
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<DoacaoResponseDto> cancelar(
            @PathVariable Long id) {

        Doacao cancelada = doacaoService.cancelar(id);

        return ResponseEntity.ok(converterParaDto(cancelada));
    }

    // Converter entidade para DTO
    private DoacaoResponseDto converterParaDto(Doacao doacao) {

        return new DoacaoResponseDto(
                doacao.getId(),
                doacao.getNomeAlimento(),
                doacao.getDescricao(),
                doacao.getQuantidade(),
                doacao.getQuantidadeDisponivel(),
                doacao.getUnidadeMedida(),
                doacao.getValidade(),
                doacao.getDataCadastro(),
                doacao.getStatus(),
                doacao.getEmpresa().getId()
        );
    }

	
	 
	
	
	
	
}
