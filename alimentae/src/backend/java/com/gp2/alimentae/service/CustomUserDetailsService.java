package com.gp2.alimentae.service;

import com.gp2.alimentae.entity.Usuario;
import com.gp2.alimentae.exception.BusinessException;
import com.gp2.alimentae.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException{
        Usuario usuario = usuarioRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(()-> new BusinessException(
                        HttpStatus.BAD_REQUEST,
                        "Usuário ou senha inválidos!"
                )
                );
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(
                        "ROLE_" + usuario.getTipoUsuario().name()
                ).build();
    }

}
