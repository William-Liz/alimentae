
package com.gp2.alimentae.repository;

import com.gp2.alimentae.entity.Ong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OngRepository extends JpaRepository<Ong, Long> {

    boolean existsByCnpj(String cnpj);

    Optional<Ong> findByUsuario_Id(Long usuarioId);
}
