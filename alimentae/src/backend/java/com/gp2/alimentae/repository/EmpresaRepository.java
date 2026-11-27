
package com.gp2.alimentae.repository;

import com.gp2.alimentae.entity.Empresa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository
        extends JpaRepository<Empresa, Long> {

    boolean existsByCnpj(String cnpj);

    boolean existsByCnpjAndIdNot(String cnpj, Long id);

    Optional<Empresa> findByUsuario_Id(Long usuarioId);

}
