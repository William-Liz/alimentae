package com.gp2.alimentae.controller;

import com.gp2.alimentae.dto.request.LoginRequestDto;
import com.gp2.alimentae.dto.response.LoginRsponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                           loginRequestDto.email().trim().toLowerCase(),
                            loginRequestDto.senha()
                    )
            );

            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> authority.startsWith("ROLE_"))
                    .findFirst()
                    .orElseThrow();

            String tipoUsuario = role.substring(5);
            Instant now = Instant.now();
            long duracao = 8000;
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("alimentae")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(duracao))
                    .subject(authentication.getName())
                    .claim("role", tipoUsuario)
                    .build();

            JwsHeader header = JwsHeader
                    .with(MacAlgorithm.HS256)
                    .build();

            String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
                    .getTokenValue();

            LoginRsponseDto response = new LoginRsponseDto(
                    token,tipoUsuario,duracao
            );
            return ResponseEntity.ok(response);
        }catch (BadCredentialsException exception){

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid username or password");
        }
    }
}
