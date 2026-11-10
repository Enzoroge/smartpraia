package com.smartpraia.smartpraia.usuario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartpraia.smartpraia.usuario.dto.LoginRequestDTO;
import com.smartpraia.smartpraia.usuario.dto.LoginResponseDTO;
import com.smartpraia.smartpraia.usuario.service.AutenticacaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService){
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto){
        LoginResponseDTO resposta = autenticacaoService.login(dto);

        return ResponseEntity.ok(resposta);
    }

}
