
package com.gp2.alimentae.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Doacao d WHERE d.id = :id")
    Optional<Doacao> buscarPorIdComBloqueio(@Param("id") Long id);
}

