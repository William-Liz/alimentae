package com.gp2.alimentae.controller;

import com.gp2.alimentae.entity.StatusSolicitacao;
import com.gp2.alimentae.service.SolicitacaoService;
import org.springframework.web.bind.annotation.*;
import com.gp2.alimentae.dto.request.SolicitacaoRequestDto;
import com.gp2.alimentae.dto.response.SolicitacaoResponseDto;
import com.gp2.alimentae.entity.Solicitacao;
import com.gp2.alimentae.entity.Usuario;
import com.gp2.alimentae.entity.Empresa;
import com.gp2.alimentae.entity.Ong;
import com.gp2.alimentae.repository.UsuarioRepository;
import com.gp2.alimentae.repository.EmpresaRepository;
import com.gp2.alimentae.repository.OngRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.http.HttpStatus;

import java.util.List;


@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final OngRepository ongRepository;

    public SolicitacaoController(SolicitacaoService solicitacaoService,
                                 UsuarioRepository usuarioRepository,
                                 EmpresaRepository empresaRepository,
                                 OngRepository ongRepository){

        this.solicitacaoService = solicitacaoService;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.ongRepository = ongRepository;
    }

    private SolicitacaoResponseDto converterParaDto(Solicitacao solicitacao){
        return new SolicitacaoResponseDto(
                solicitacao.getId(),
                solicitacao.getQuantidadeSolicitada(),
                solicitacao.getDataSolicitacao(),
                solicitacao.getStatus(),
                solicitacao.getOng().getId(),
                solicitacao.getDoacao().getId()
        );
    }

    private Usuario buscarUsuarioAutenticado(Jwt jwt){
        return usuarioRepository.findByEmail(jwt.getSubject())
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));
    }

    private Empresa buscarEmpresaAutenticada(Jwt jwt){
        Usuario usuario = buscarUsuarioAutenticado(jwt);

        return empresaRepository.findByUsuario_Id(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Empresa do usuário não encontrada."));
    }

    private Ong buscarOngAutenticada(Jwt jwt){
        Usuario usuario = buscarUsuarioAutenticado(jwt);

        return ongRepository.findByUsuario_Id(usuario.getId())
                .orElseThrow(() -> new RuntimeException("ONG do usuário não encontrada."));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ONG')")
    public SolicitacaoResponseDto criar(@RequestBody SolicitacaoRequestDto dados,
                                        @AuthenticationPrincipal Jwt jwt){
        Ong ong = buscarOngAutenticada(jwt);

        Solicitacao solicitacao = solicitacaoService.solicitar(
                ong.getId(),
                dados.doacaoId(),
                dados.quantidadeSolicitada()
        );

    return new SolicitacaoResponseDto(
            solicitacao.getId(),
            solicitacao.getQuantidadeSolicitada(),
            solicitacao.getDataSolicitacao(),
            solicitacao.getStatus(),
            solicitacao.getOng().getId(),
            solicitacao.getDoacao().getId()
    );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ONG', 'EMPRESA')")
    public List<SolicitacaoResponseDto> listar(
            @AuthenticationPrincipal Jwt jwt){

        String tipoUsuario = jwt.getClaimAsString("role");

        List<Solicitacao> solicitacoes;

        if ("ONG".equals(tipoUsuario)){
            Ong ong = buscarOngAutenticada(jwt);
            solicitacoes = solicitacaoService.listarPorOng(ong.getId());
        }else {
            Empresa empresa = buscarEmpresaAutenticada(jwt);
            solicitacoes = solicitacaoService.listarPorEmpresa(empresa.getId());
        }
        return solicitacoes.stream().map(this::converterParaDto).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ONG', 'EMPRESA')")
    public SolicitacaoResponseDto buscarPorId(@PathVariable Long id,
                                              @AuthenticationPrincipal Jwt jwt){

        Solicitacao solicitacao;

        String tipoUsuario = jwt.getClaimAsString("role");

        if ("ONG".equals(tipoUsuario)){
            Ong ong = buscarOngAutenticada(jwt);
            solicitacao = solicitacaoService.buscarPorIdEOng(id, ong.getId());
        }else {
            Empresa empresa = buscarEmpresaAutenticada(jwt);
            solicitacao = solicitacaoService.buscarPorIdEEmpresa(id, empresa.getId());
        }
        return converterParaDto(solicitacao);
    }

    @GetMapping("/ong")
    @PreAuthorize("hasRole('ONG')")
    public List<SolicitacaoResponseDto> listarPorOng(
            @AuthenticationPrincipal Jwt jwt){
        Ong ong = buscarOngAutenticada(jwt);

        return solicitacaoService.listarPorOng(
                ong.getId())
                .stream()
                .map(this::converterParaDto)
                .toList();
    }

    @GetMapping("/doacao/{doacaoId}")
    @PreAuthorize("hasRole('EMPRESA')")
    public List<SolicitacaoResponseDto> listarPorDoacao(
            @PathVariable Long doacaoId,
            @AuthenticationPrincipal Jwt jwt){
        Empresa empresa = buscarEmpresaAutenticada(jwt);

        List<Solicitacao> solicitacoes =
                solicitacaoService.listarPorEmpresa(empresa.getId());

        return solicitacoes.stream().filter(s -> s.getDoacao()
                        .getId()
                        .equals(doacaoId))
                .map(this::converterParaDto)
                .toList();
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ONG', 'EMPRESA')")
    public List<SolicitacaoResponseDto> listarPorStatus(
            @PathVariable StatusSolicitacao status,
            @AuthenticationPrincipal Jwt jwt){
        String tipoUsuario = jwt.getClaimAsString("role");

        List<Solicitacao> solicitacoes;

        if ("ONG".equals(tipoUsuario)){
            Ong ong = buscarOngAutenticada(jwt);
            solicitacoes = solicitacaoService.listarPorOng(ong.getId());
        }else {
            Empresa empresa = buscarEmpresaAutenticada(jwt);
            solicitacoes = solicitacaoService.listarPorEmpresa(empresa.getId());
        }
        return solicitacoes.stream()
                .filter(s -> s.getStatus() == status)
                .map(this::converterParaDto)
                .toList();
    }

    @PostMapping("/{id}/aprovar")
    @PreAuthorize("hasRole('EMPRESA')")
    public SolicitacaoResponseDto aprovar(@PathVariable Long id,
                                          @AuthenticationPrincipal Jwt jwt){

        Empresa empresa = buscarEmpresaAutenticada(jwt);

        Solicitacao solicitacao = solicitacaoService.aprovar(id, empresa.getId());

        return converterParaDto(solicitacao);
    }

    @PostMapping("/{id}/recusar")
    @PreAuthorize("hasRole('EMPRESA')")
    public SolicitacaoResponseDto recusar(@PathVariable Long id,
                                          @AuthenticationPrincipal Jwt jwt){
        Empresa empresa = buscarEmpresaAutenticada(jwt);

        Solicitacao solicitacao = solicitacaoService.recusar(id, empresa.getId());

        return converterParaDto(solicitacao);
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ONG')")
    public SolicitacaoResponseDto cancelar(@PathVariable Long id,
                                           @AuthenticationPrincipal Jwt jwt){
        Ong ong = buscarOngAutenticada(jwt);

        Solicitacao solicitacao = solicitacaoService.cancelar(id, ong.getId());

        return converterParaDto(solicitacao);
    }

    @PostMapping("/{id}/concluir")
    @PreAuthorize("hasRole('ONG')")
    public SolicitacaoResponseDto concluir(@PathVariable Long id,
                                           @AuthenticationPrincipal Jwt jwt){
        Ong ong = buscarOngAutenticada(jwt);

        Solicitacao solicitacao = solicitacaoService.concluir(id, ong.getId());

        return converterParaDto(solicitacao);
    }
}
