
package com.gp2.alimentae.service;

import com.gp2.alimentae.entity.Empresa;
import com.gp2.alimentae.entity.Ong;
import com.gp2.alimentae.entity.TipoUsuario;
import com.gp2.alimentae.entity.Usuario;
import com.gp2.alimentae.exception.BusinessException;
import com.gp2.alimentae.repository.EmpresaRepository;
import com.gp2.alimentae.repository.OngRepository;
import com.gp2.alimentae.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
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
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "ONG não encontrada."
                        ));
    }

    @Transactional
    public Ong cadastrar(Ong ong) {

        if (ong.getCnpj() == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "O CNPJ deve ser informado."
            );
        }

        String cnpj = ong.getCnpj().replaceAll("\\D", "");

        if (cnpj.length() != 14) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "O CNPJ deve possuir 14 dígitos."
            );
        }

        if (ongRepository.existsByCnpj(cnpj)) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "Este CNPJ já está cadastrado."
            );
        }

        if (ong.getUsuario() == null
                || ong.getUsuario().getId() == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "É necessário informar o usuário responsável."
            );
        }

        Long usuarioId = ong.getUsuario().getId();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Usuário não encontrado."
                        ));

        if (usuario.getTipoUsuario() != TipoUsuario.ONG) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "O usuário informado não é do tipo ONG."
            );
        }

        if (ongRepository.findByUsuario_Id(usuarioId).isPresent()
                || empresaRepository.findByUsuario_Id(usuarioId).isPresent()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "Este usuário já possui um perfil institucional."
            );
        }

        ong.setCnpj(cnpj);
        ong.setUsuario(usuario);

        return ongRepository.save(ong);
    }
}
