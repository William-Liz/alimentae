
package com.gp2.alimentae.repository;

import com.gp2.alimentae.entity.Solicitacao;
import com.gp2.alimentae.entity.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    List<Solicitacao> findByOng_Id(Long ongId);

    List<Solicitacao> findByDoacao_Id(Long doacaoId);

    List<Solicitacao> findByStatus(StatusSolicitacao status);
}

