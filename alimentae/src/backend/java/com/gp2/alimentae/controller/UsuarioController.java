package com.gp2.alimentae.controller;

import com.gp2.alimentae.dto.request.UsuarioRequestDto;
import com.gp2.alimentae.dto.response.UsuarioResponseDto;
import com.gp2.alimentae.entity.Usuario;
import com.gp2.alimentae.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
// cadastro
    @PostMapping
    public ResponseEntity<UsuarioResponseDto> cadastro(
            @RequestBody UsuarioRequestDto request){
        Usuario usuario = new Usuario();

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());
        usuario.setTipoUsuario(request.tipoUsuario());

        Usuario salvo = usuarioService.cadastrar(usuario);

        UsuarioResponseDto response = new UsuarioResponseDto(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getTipoUsuario()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //listar
    @GetMapping
    public  ResponseEntity<List<UsuarioResponseDto>> listar(){
        List<UsuarioResponseDto> usuarios = usuarioService.listar().stream().map(
                usuario -> new UsuarioResponseDto(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getTipoUsuario()
                )).toList();

        return ResponseEntity.ok(usuarios);
    }

    //busca por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id){

        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

}
