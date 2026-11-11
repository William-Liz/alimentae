
package com.gp2.alimentae.repository;

import com.gp2.alimentae.entity.Doacao;
import com.gp2.alimentae.entity.StatusDoacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {

    List<Doacao> findByStatus(StatusDoacao status);

    List<Doacao> findByEmpresa_Id(Long empresaId);

    List<Doacao> findByStatusAndQuantidadeDisponivelGreaterThan(
            StatusDoacao status,
            BigDecimal quantidade
    );
}

