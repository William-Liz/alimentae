
package com.gp2.alimentae.service;

import com.gp2.alimentae.entity.Empresa;
import com.gp2.alimentae.entity.Ong;
import com.gp2.alimentae.entity.TipoUsuario;
import com.gp2.alimentae.entity.Usuario;
import com.gp2.alimentae.exception.BusinessException;
import com.gp2.alimentae.repository.EmpresaRepository;
import com.gp2.alimentae.repository.OngRepository;
import com.gp2.alimentae.repository.UsuarioRepository;
import com.gp2.alimentae.repository.SolicitacaoRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;

@Service
public class OngService {

    private final OngRepository ongRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final SolicitacaoRepository solicitacaoRepository;

    public OngService(
            OngRepository ongRepository,
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository,
            SolicitacaoRepository solicitacaoRepository) {

        this.ongRepository = ongRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.solicitacaoRepository = solicitacaoRepository;
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
    public Ong cadastrar(Ong ong, String emailAutenticado) {

        // Busca o usuário autenticado pelo e-mail do JWT
        Usuario usuario = usuarioRepository
                .findByEmail(emailAutenticado.trim().toLowerCase())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Usuário autenticado não encontrado."
                        ));

        // Somente usuários do tipo ONG podem cadastrar um perfil de ONG
        if (usuario.getTipoUsuario() != TipoUsuario.ONG) {
            throw new AccessDeniedException(
                    "Somente usuários do tipo ONG podem cadastrar este perfil."
            );
        }

        // Verifica se o usuário já possui um perfil institucional
        if (ongRepository.findByUsuario_Id(usuario.getId()).isPresent()
                || empresaRepository.findByUsuario_Id(usuario.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Este usuário já possui um perfil institucional."
            );
        }

        // Valida o CNPJ
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

        ong.setCnpj(cnpj);

        // Vincula a ONG ao usuário autenticado
        ong.setUsuario(usuario);

        return ongRepository.save(ong);
    }


    @Transactional
    public Ong atualizar(
            Long id,
            Ong dadosAtualizados,
            String emailAutenticado) {

        // Busca a ONG que será atualizada
        Ong ongExistente = buscarPorId(id);

        // Identifica o usuário autenticado pelo JWT
        Usuario usuarioAutenticado = usuarioRepository
                .findByEmail(emailAutenticado.trim().toLowerCase())
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Usuário não encontrado."
                        ));

        // Somente usuários do tipo ONG podem atualizar um perfil de ONG
        if (usuarioAutenticado.getTipoUsuario() != TipoUsuario.ONG) {
            throw new AccessDeniedException(
                    "Somente usuários do tipo ONG podem atualizar este perfil."
            );
        }

        // Verifica se a ONG pertence ao usuário autenticado
        if (ongExistente.getUsuario() == null
                || ongExistente.getUsuario().getId() == null
                || !ongExistente.getUsuario().getId()
                .equals(usuarioAutenticado.getId())) {

            throw new AccessDeniedException(
                    "Você não tem permissão para atualizar esta ONG."
            );
        }

        // Valida o CNPJ
        if (dadosAtualizados.getCnpj() == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "O CNPJ deve ser informado."
            );
        }

        String cnpj = dadosAtualizados.getCnpj()
                .replaceAll("\\D", "");

        if (cnpj.length() != 14) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "O CNPJ deve possuir 14 dígitos."
            );
        }

        // Verifica se outra ONG já utiliza esse CNPJ
        if (ongRepository.existsByCnpjAndIdNot(cnpj, id)) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "Este CNPJ já está cadastrado para outra ONG."
            );
        }

        // Atualiza os dados da ONG
        ongExistente.setNome(dadosAtualizados.getNome());
        ongExistente.setCnpj(cnpj);
        ongExistente.setEndereco(dadosAtualizados.getEndereco());
        ongExistente.setAreaAtuacao(dadosAtualizados.getAreaAtuacao());
        ongExistente.setTelefone(dadosAtualizados.getTelefone());

        return ongRepository.save(ongExistente);
    }


    // Buscar a ONG do usuário autenticado
    @Transactional(readOnly = true)
    public Ong buscarMinhaOng(String emailAutenticado) {

        Usuario usuario = usuarioRepository
                .findByEmail(emailAutenticado.trim().toLowerCase())
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Usuário autenticado não encontrado."
                        ));

        if (usuario.getTipoUsuario() != TipoUsuario.ONG) {
            throw new AccessDeniedException(
                    "Somente usuários do tipo ONG podem acessar esta funcionalidade."
            );
        }

        return ongRepository.findByUsuario_Id(usuario.getId())
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Perfil de ONG não encontrado."
                        ));
    }

    // Atualizar a ONG do usuário autenticado
    @Transactional
    public Ong atualizarMinhaOng(
            Ong dadosAtualizados,
            String emailAutenticado) {

        Ong ongExistente = buscarMinhaOng(emailAutenticado);

        return atualizar(
                ongExistente.getId(),
                dadosAtualizados,
                emailAutenticado
        );
    }

    // Excluir a ONG do usuário autenticado
    @Transactional
    public void excluirMinhaOng(String emailAutenticado) {

        Ong ong = buscarMinhaOng(emailAutenticado);

        if (solicitacaoRepository.existsByOng_Id(ong.getId())) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "Não é possível excluir esta ONG porque existem solicitações vinculadas."
            );
        }

        ongRepository.delete(ong);
    }

}
