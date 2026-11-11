
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
public class OngService {

    private final OngRepository ongRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;

    public OngService(
            OngRepository ongRepository,
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository) {

        this.ongRepository = ongRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
    }

    @Transactional(readOnly = true)
    public List<Ong> listar() {
        return ongRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Ong buscarPorId(Long id) {
        return ongRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "ONG não encontrada."
                        ));
    }

    @Transactional
    public Ong cadastrar(Ong ong) {

        if (ong.getCnpj() == null) {
            throw new IllegalArgumentException(
                    "O CNPJ deve ser informado."
            );
        }

        String cnpj = ong.getCnpj().replaceAll("\\D", "");

        if (cnpj.length() != 14) {
            throw new IllegalArgumentException(
                    "O CNPJ deve possuir 14 dígitos."
            );
        }

        if (ongRepository.existsByCnpj(cnpj)) {
            throw new IllegalArgumentException(
                    "Este CNPJ já está cadastrado."
            );
        }

        if (ong.getUsuario() == null
                || ong.getUsuario().getId() == null) {
            throw new IllegalArgumentException(
                    "É necessário informar o usuário responsável."
            );
        }

        Long usuarioId = ong.getUsuario().getId();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Usuário não encontrado."
                        ));

        if (usuario.getTipoUsuario() != TipoUsuario.ONG) {
            throw new IllegalArgumentException(
                    "O usuário informado não é do tipo ONG."
            );
        }

        if (ongRepository.findByUsuario_Id(usuarioId).isPresent()
                || empresaRepository.findByUsuario_Id(usuarioId).isPresent()) {
            throw new IllegalArgumentException(
                    "Este usuário já possui um perfil institucional."
            );
        }

        ong.setCnpj(cnpj);
        ong.setUsuario(usuario);

        return ongRepository.save(ong);
    }
}
