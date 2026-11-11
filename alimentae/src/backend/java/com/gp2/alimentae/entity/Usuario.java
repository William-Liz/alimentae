package com.gp2.alimentae.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 120)
        private String nome;

        @Column(nullable = false, length = 150, unique = true)
        private String email;

        @Column(nullable = false, length = 255)
        private String senha;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_usuario", nullable = false, length = 20)
        private TipoUsuario tipoUsuario;

        public Usuario() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }

        public TipoUsuario getTipoUsuario() {
            return tipoUsuario;
        }

        public void setTipoUsuario(TipoUsuario tipoUsuario) {
            this.tipoUsuario = tipoUsuario;
        }
    }

