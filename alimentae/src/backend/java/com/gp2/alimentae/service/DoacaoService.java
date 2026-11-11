
package com.gp2.alimentae.service;

import com.gp2.alimentae.entity.Doacao;
import com.gp2.alimentae.entity.Empresa;
import com.gp2.alimentae.entity.StatusDoacao;
import com.gp2.alimentae.entity.StatusSolicitacao;

import com.gp2.alimentae.repository.DoacaoRepository;
import com.gp2.alimentae.repository.EmpresaRepository;
import com.gp2.alimentae.repository.SolicitacaoRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;
    private final EmpresaRepository empresaRepository;
    private final SolicitacaoRepository solicitacaoRepository;

    public DoacaoService(
            DoacaoRepository doacaoRepository,
            EmpresaRepository empresaRepository,
            SolicitacaoRepository solicitacaoRepository) {

        this.doacaoRepository = doacaoRepository;
        this.empresaRepository = empresaRepository;
        this.solicitacaoRepository = solicitacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<Doacao> listar() {
        return doacaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Doacao> listarDisponiveis() {
        return doacaoRepository
                .findByStatusAndQuantidadeDisponivelGreaterThan(
                        StatusDoacao.DISPONIVEL,
                        BigDecimal.ZERO
                );
    }

    @Transactional(readOnly = true)
    public Doacao buscarPorId(Long id) {
        return doacaoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Doação não encontrada."
                        ));
    }

    @Transactional(readOnly = true)
    public List<Doacao> listarPorEmpresa(Long empresaId) {
        return doacaoRepository.findByEmpresa_Id(empresaId);
    }

    @Transactional
    public Doacao cadastrar(Doacao doacao) {

        if (doacao.getNomeAlimento() == null
                || doacao.getNomeAlimento().isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do alimento deve ser informado."
            );
        }

        if (doacao.getQuantidade() == null
                || doacao.getQuantidade().signum() <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }

        if (doacao.getUnidadeMedida() == null) {
            throw new IllegalArgumentException(
                    "A unidade de medida deve ser informada."
            );
        }

        if (doacao.getValidade() == null
                || doacao.getValidade().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Informe uma data de validade válida."
            );
        }

        if (doacao.getEmpresa() == null
                || doacao.getEmpresa().getId() == null) {
            throw new IllegalArgumentException(
                    "É necessário informar a empresa responsável."
            );
        }

        Long empresaId = doacao.getEmpresa().getId();

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Empresa não encontrada."
                        ));

        doacao.setEmpresa(empresa);

        // A quantidade disponível começa igual à quantidade cadastrada.
        doacao.setQuantidadeDisponivel(doacao.getQuantidade());

        // Toda nova doação começa como disponível.
        doacao.setStatus(StatusDoacao.DISPONIVEL);

        return doacaoRepository.save(doacao);
    }

    @Transactional
    public Doacao cancelar(Long id) {

        Doacao doacao = buscarPorId(id);

        if (doacao.getStatus() == StatusDoacao.CANCELADA) {
            throw new IllegalArgumentException(
                    "Esta doação já está cancelada."
            );
        }

        boolean possuiSolicitacoesAtivas =
                solicitacaoRepository.findByDoacao_Id(id)
                        .stream()
                        .anyMatch(solicitacao ->
                                solicitacao.getStatus()
                                        == StatusSolicitacao.PENDENTE
                                        || solicitacao.getStatus()
                                        == StatusSolicitacao.APROVADA
                        );

        if (possuiSolicitacoesAtivas) {
            throw new IllegalArgumentException(
                    "Resolva as solicitações pendentes ou aprovadas "
                            + "antes de cancelar a doação."
            );
        }

        doacao.setStatus(StatusDoacao.CANCELADA);
        doacao.setQuantidadeDisponivel(BigDecimal.ZERO);

        return doacaoRepository.save(doacao);
    }
}
