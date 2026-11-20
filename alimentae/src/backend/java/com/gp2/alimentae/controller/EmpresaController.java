package com.gp2.alimentae.controller;

import com.gp2.alimentae.dto.request.EmpresaRequestDto;
import com.gp2.alimentae.dto.response.EmpresaResponseDto;
import com.gp2.alimentae.entity.Empresa;
import com.gp2.alimentae.service.EmpresaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping()
    public List<Empresa> listarEmpresa() {
        return empresaService.listar();
    }

    @GetMapping("/{id}")
    public Empresa buscarPorId(@PathVariable Long id) {
        return empresaService.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void deletarEmpresa(@PathVariable Long id) {
        Empresa empresa = empresaService.buscarPorId(id);
        empresaService.deletar(empresa);
    }

    @PutMapping("/{id}")
    public EmpresaResponseDto atualizar(@PathVariable Long id, @RequestBody EmpresaRequestDto empresa) {
        return empresaService.atualizar(empresa, id);
    }

}
