package com.smartpraia.smartpraia.usuario.controller;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smartpraia.smartpraia.usuario.service.UsuarioService;
import com.smartpraia.smartpraia.usuario.model.Usuario;
import com.smartpraia.smartpraia.usuario.dto.UsuarioRequestDTO;
import java.util.List;

@RestController
// @RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service){
        this.service = service;
    }

    @PostMapping("/usuario")
    public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody UsuarioRequestDTO dto){
        Usuario usuario = service.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarTodos(){
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(service.buscarPorId(id));
    }

}
