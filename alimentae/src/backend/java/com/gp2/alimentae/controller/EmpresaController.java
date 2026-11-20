package com.gp2.alimentae.controller;

import com.gp2.alimentae.entity.Empresa;
import com.gp2.alimentae.service.EmpresaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping("/empresas")
    public List<Empresa> listarEmpresa() {
        return empresaService.listar();
    }

    @GetMapping("/empresas/{id}")
    public Empresa buscarPorId(@PathVariable Long id) {
        return empresaService.buscarPorId(id);
    }

    @DeleteMapping("empresa/{id}")
    public void deletarEmpresa(@PathVariable Long id) {
        Empresa empresa = empresaService.buscarPorId(id);
        empresaService.deletar(empresa);
    }


}
