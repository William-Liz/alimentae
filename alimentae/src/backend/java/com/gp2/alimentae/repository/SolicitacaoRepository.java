
package com.gp2.alimentae.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

import com.gp2.alimentae.entity.Solicitacao;
import com.gp2.alimentae.entity.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    List<Solicitacao> findByOng_Id(Long ongId);

    List<Solicitacao> findByDoacao_Id(Long doacaoId);

    List<Solicitacao> findByStatus(StatusSolicitacao status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Solicitacao s WHERE s.id = :id")
    Optional<Solicitacao> buscarPorIdComBloqueio(@Param("id") Long id);
}

