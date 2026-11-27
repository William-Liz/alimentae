
package com.gp2.alimentae.service;

import com.gp2.alimentae.dto.request.EmpresaRequestDto;
import com.gp2.alimentae.dto.response.EmpresaResponseDto;
import com.gp2.alimentae.entity.Empresa;
import com.gp2.alimentae.entity.TipoUsuario;
import com.gp2.alimentae.entity.Usuario;
import com.gp2.alimentae.exception.BusinessException;
import com.gp2.alimentae.repository.EmpresaRepository;
import com.gp2.alimentae.repository.OngRepository;
import com.gp2.alimentae.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // Cadastrar empresa
    @Transactional
    public EmpresaResponseDto cadastrar(
            EmpresaRequestDto request,
            String emailAutenticado) {

        Usuario usuario = buscarUsuarioAutenticado(emailAutenticado);

        validarTipoEmpresa(usuario);

        // Verifica se o usuário já possui um perfil institucional
        if (empresaRepository.findByUsuario_Id(usuario.getId()).isPresent()
                || ongRepository.findByUsuario_Id(usuario.getId()).isPresent()) {

            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "Este usuário já possui um perfil institucional."
            );
        }

        String cnpj = normalizarCnpj(request.cnpj());

        if (empresaRepository.existsByCnpj(cnpj)) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "Este CNPJ já está cadastrado."
            );
        }

        Empresa empresa = new Empresa();

        empresa.setNomeFantasia(request.nomeFantasia().trim());
        empresa.setCnpj(cnpj);
        empresa.setEndereco(request.endereco().trim());
        empresa.setTelefone(request.telefone().trim());
        empresa.setUsuario(usuario);

        Empresa salva = empresaRepository.save(empresa);

        return paraDTO(salva);
    }

    // Buscar empresa do usuário autenticado
    @Transactional(readOnly = true)
    public EmpresaResponseDto buscarMinhaEmpresa(String emailAutenticado) {

        Usuario usuario = buscarUsuarioAutenticado(emailAutenticado);

        validarTipoEmpresa(usuario);

        Empresa empresa = empresaRepository
                .findByUsuario_Id(usuario.getId())
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Perfil de empresa não encontrado."
                        )
                );

        return paraDTO(empresa);
    }

    // Atualizar empresa do usuário autenticado
    @Transactional
    public EmpresaResponseDto atualizarMinhaEmpresa(
            EmpresaRequestDto request,
            String emailAutenticado) {

        Usuario usuario = buscarUsuarioAutenticado(emailAutenticado);

        validarTipoEmpresa(usuario);

        Empresa empresa = empresaRepository
                .findByUsuario_Id(usuario.getId())
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Perfil de empresa não encontrado."
                        )
                );

        String cnpj = normalizarCnpj(request.cnpj());

        if (empresaRepository.existsByCnpjAndIdNot(
                cnpj,
                empresa.getId())) {

            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "Este CNPJ já está cadastrado para outra empresa."
            );
        }

        empresa.setNomeFantasia(request.nomeFantasia().trim());
        empresa.setCnpj(cnpj);
        empresa.setEndereco(request.endereco().trim());
        empresa.setTelefone(request.telefone().trim());

        Empresa atualizada = empresaRepository.save(empresa);

        return paraDTO(atualizada);
    }

    // Excluir empresa do usuário autenticado
    @Transactional
    public void deletarMinhaEmpresa(String emailAutenticado) {

        Usuario usuario = buscarUsuarioAutenticado(emailAutenticado);

        validarTipoEmpresa(usuario);

        Empresa empresa = empresaRepository
                .findByUsuario_Id(usuario.getId())
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Perfil de empresa não encontrado."
                        )
                );

        empresaRepository.delete(empresa);
    }

    // Buscar usuário pelo e-mail contido no JWT
    private Usuario buscarUsuarioAutenticado(String email) {

        return usuarioRepository
                .findByEmail(email.trim().toLowerCase())
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Usuário autenticado não encontrado."
                        )
                );
    }

    // Verificar tipo de usuário
    private void validarTipoEmpresa(Usuario usuario) {

        if (usuario.getTipoUsuario() != TipoUsuario.EMPRESA) {
            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    "Somente usuários do tipo EMPRESA podem acessar esta funcionalidade."
            );
        }
    }

    // Remover caracteres não numéricos e validar CNPJ
    private String normalizarCnpj(String cnpj) {

        String cnpjLimpo = cnpj.replaceAll("\\D", "");

        if (cnpjLimpo.length() != 14) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "O CNPJ deve possuir 14 dígitos."
            );
        }

        return cnpjLimpo;
    }

    // Converter entidade para DTO de resposta
    private EmpresaResponseDto paraDTO(Empresa empresa) {

        return new EmpresaResponseDto(
                empresa.getId(),
                empresa.getNomeFantasia(),
                empresa.getCnpj(),
                empresa.getEndereco(),
                empresa.getTelefone(),
                empresa.getUsuario().getId()
        );
    }
}
