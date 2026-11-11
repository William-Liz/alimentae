
package com.gp2.alimentae.service;

import com.gp2.alimentae.entity.Empresa;
import com.gp2.alimentae.entity.Ong;
import com.gp2.alimentae.entity.TipoUsuario;
import com.gp2.alimentae.entity.Usuario;
import com.gp2.alimentae.repository.EmpresaRepository;
import com.gp2.alimentae.repository.OngRepository;
import com.gp2.alimentae.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final OngRepository ongRepository;

    public EmpresaService(
            EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository,
            OngRepository ongRepository) {

        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.ongRepository = ongRepository;
    }

    @Transactional(readOnly = true)
    public List<Empresa> listar() {
        return empresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Empresa buscarPorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Empresa não encontrada."
                        ));
    }

    @Transactional
    public Empresa cadastrar(Empresa empresa) {

        if (empresa.getCnpj() == null) {
            throw new IllegalArgumentException(
                    "O CNPJ deve ser informado."
            );
        }

        String cnpj = empresa.getCnpj().replaceAll("\\D", "");

        if (cnpj.length() != 14) {
            throw new IllegalArgumentException(
                    "O CNPJ deve possuir 14 dígitos."
            );
        }

        if (empresaRepository.existsByCnpj(cnpj)) {
            throw new IllegalArgumentException(
                    "Este CNPJ já está cadastrado."
            );
        }

        if (empresa.getUsuario() == null
                || empresa.getUsuario().getId() == null) {
            throw new IllegalArgumentException(
                    "É necessário informar o usuário responsável."
            );
        }

        Long usuarioId = empresa.getUsuario().getId();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Usuário não encontrado."
                        ));

        if (usuario.getTipoUsuario() != TipoUsuario.EMPRESA) {
            throw new IllegalArgumentException(
                    "O usuário informado não é do tipo EMPRESA."
            );
        }

        if (empresaRepository.findByUsuario_Id(usuarioId).isPresent()
                || ongRepository.findByUsuario_Id(usuarioId).isPresent()) {
            throw new IllegalArgumentException(
                    "Este usuário já possui um perfil institucional."
            );
        }

        empresa.setCnpj(cnpj);
        empresa.setUsuario(usuario);

        return empresaRepository.save(empresa);
    }
}
