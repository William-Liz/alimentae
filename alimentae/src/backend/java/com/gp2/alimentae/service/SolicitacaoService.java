
package com.gp2.alimentae.service;

import com.gp2.alimentae.entity.Doacao;
import com.gp2.alimentae.entity.Ong;
import com.gp2.alimentae.entity.Solicitacao;
import com.gp2.alimentae.entity.StatusDoacao;
import com.gp2.alimentae.entity.StatusSolicitacao;

import com.gp2.alimentae.exception.BusinessException;
import com.gp2.alimentae.repository.DoacaoRepository;
import com.gp2.alimentae.repository.OngRepository;
import com.gp2.alimentae.repository.SolicitacaoRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final DoacaoRepository doacaoRepository;
    private final OngRepository ongRepository;

    public SolicitacaoService(
            SolicitacaoRepository solicitacaoRepository,
            DoacaoRepository doacaoRepository,
            OngRepository ongRepository) {

        this.solicitacaoRepository = solicitacaoRepository;
        this.doacaoRepository = doacaoRepository;
        this.ongRepository = ongRepository;
    }

    @Transactional(readOnly = true)
    public List<Solicitacao> listar() {
        return solicitacaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Solicitacao> listarPorOng(Long ongId) {
        return solicitacaoRepository.findByOng_Id(ongId);
    }

    @Transactional(readOnly = true)
    public List<Solicitacao> listarPorDoacao(Long doacaoId) {
        return solicitacaoRepository.findByDoacao_Id(doacaoId);
    }

    @Transactional
    public Solicitacao solicitar(
            Long ongId,
            Long doacaoId,
            BigDecimal quantidadeSolicitada) {

        if (quantidadeSolicitada == null
                || quantidadeSolicitada.signum() <= 0) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "A quantidade solicitada deve ser maior que zero."
            );
        }

        Ong ong = ongRepository.findById(ongId)
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "ONG não encontrada."
                        ));

        Doacao doacao = doacaoRepository
                .buscarPorIdComBloqueio(doacaoId)
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Doação não encontrada."
                        ));

        if (doacao.getStatus() != StatusDoacao.DISPONIVEL) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Esta doação não está disponível."
            );
        }

        if (doacao.getValidade().isBefore(LocalDate.now())) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Esta doação está vencida."
            );
        }

        if (quantidadeSolicitada.compareTo(
                doacao.getQuantidadeDisponivel()) > 0) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "A quantidade solicitada é maior que a disponível."
            );
        }

        Solicitacao solicitacao = new Solicitacao();

        solicitacao.setOng(ong);
        solicitacao.setDoacao(doacao);
        solicitacao.setQuantidadeSolicitada(quantidadeSolicitada);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);

        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public Solicitacao aprovar(Long solicitacaoId, Long empresaId) {

        Solicitacao solicitacao =
                buscarSolicitacaoComBloqueio(solicitacaoId);

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Somente solicitações pendentes podem ser aprovadas."
            );
        }

        Doacao doacao = doacaoRepository.buscarPorIdComBloqueio(
                solicitacao.getDoacao().getId()
        ).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "Doação não encontrada.")
        );

        if (!doacao.getEmpresa().getId().equals(empresaId)) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Esta doação não pertence à empresa informada."
            );
        }

        if (doacao.getStatus() != StatusDoacao.DISPONIVEL) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Esta doação não está disponível."
            );
        }

        BigDecimal quantidade = solicitacao.getQuantidadeSolicitada();

        if (doacao.getQuantidadeDisponivel().compareTo(quantidade) < 0) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Não há quantidade suficiente para aprovar esta solicitação."
            );
        }

        BigDecimal restante =
                doacao.getQuantidadeDisponivel().subtract(quantidade);

        doacao.setQuantidadeDisponivel(restante);

        if (restante.compareTo(BigDecimal.ZERO) == 0) {
            doacao.setStatus(StatusDoacao.ESGOTADA);
        }

        solicitacao.setStatus(StatusSolicitacao.APROVADA);

        doacaoRepository.save(doacao);

        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public Solicitacao recusar(Long solicitacaoId, Long empresaId) {

        Solicitacao solicitacao =
                buscarSolicitacaoComBloqueio(solicitacaoId);

        if (!solicitacao.getDoacao().getEmpresa().getId()
                .equals(empresaId)) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Esta solicitação não pertence à empresa informada."
            );
        }

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Somente solicitações pendentes podem ser recusadas."
            );
        }

        solicitacao.setStatus(StatusSolicitacao.RECUSADA);

        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public Solicitacao cancelar(Long solicitacaoId, Long ongId) {

        Solicitacao solicitacao =
                buscarSolicitacaoComBloqueio(solicitacaoId);

        if (!solicitacao.getOng().getId().equals(ongId)) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Esta solicitação não pertence à ONG informada."
            );
        }

        if (solicitacao.getStatus() == StatusSolicitacao.PENDENTE) {

            solicitacao.setStatus(StatusSolicitacao.CANCELADA);

        } else if (solicitacao.getStatus()
                == StatusSolicitacao.APROVADA) {

            Doacao doacao = doacaoRepository.buscarPorIdComBloqueio(
                    solicitacao.getDoacao().getId()
            ).orElseThrow(() ->
                    new BusinessException(HttpStatus.NOT_FOUND, "Doação não encontrada.")
            );

            if (doacao.getStatus() == StatusDoacao.CANCELADA) {
                throw new BusinessException(
                        HttpStatus.BAD_REQUEST,
                        "Não é possível devolver quantidade a uma doação cancelada."
                );
            }

            BigDecimal quantidadeRestaurada =
                    doacao.getQuantidadeDisponivel().add(
                            solicitacao.getQuantidadeSolicitada()
                    );

            if (quantidadeRestaurada.compareTo(
                    doacao.getQuantidade()) > 0) {
                throw new BusinessException(
                        HttpStatus.BAD_REQUEST,
                        "A quantidade restaurada ultrapassaria a quantidade original."
                );
            }

            doacao.setQuantidadeDisponivel(quantidadeRestaurada);

            doacao.setStatus(StatusDoacao.DISPONIVEL);

            doacaoRepository.save(doacao);

            solicitacao.setStatus(StatusSolicitacao.CANCELADA);

        } else {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Esta solicitação não pode ser cancelada."
            );
        }

        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public Solicitacao concluir(Long solicitacaoId, Long ongId) {

        Solicitacao solicitacao =
                buscarSolicitacaoComBloqueio(solicitacaoId);

        if (!solicitacao.getOng().getId().equals(ongId)) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Esta solicitação não pertence à ONG informada."
            );
        }

        if (solicitacao.getStatus() != StatusSolicitacao.APROVADA) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Somente solicitações aprovadas podem ser concluídas."
            );
        }

        solicitacao.setStatus(StatusSolicitacao.CONCLUIDA);

        return solicitacaoRepository.save(solicitacao);
    }

    private Solicitacao buscarSolicitacaoComBloqueio(Long id) {
        return solicitacaoRepository.buscarPorIdComBloqueio(id)
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Solicitação não encontrada."
                        ));
    }
}

