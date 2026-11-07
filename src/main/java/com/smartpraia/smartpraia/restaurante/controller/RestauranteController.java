package com.smartpraia.smartpraia.restaurante.controller;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;


import com.smartpraia.smartpraia.restaurante.model.Restaurante;
import com.smartpraia.smartpraia.restaurante.service.RestauranteService;
import com.smartpraia.smartpraia.restaurante.dto.RestauranteRequestDTO;

import org.springframework.http.ResponseEntity;

@RestController
// @RequestMapping("/restaurantes")
public class RestauranteController {

    private final RestauranteService service;

    public RestauranteController(RestauranteService service){
        this.service = service;
    }

    @PostMapping("/restaurante")
    public ResponseEntity<Restaurante> cadastrar(@Valid @RequestBody RestauranteRequestDTO dto){
        Restaurante novoRestaurante = service.cadastrar(dto);
        return ResponseEntity.status(201).body(novoRestaurante);
    }

    
    @GetMapping("/restaurantes")
    public ResponseEntity<List<Restaurante>> listarTodos(){
        List<Restaurante> restaurantes = service.listarTodos();
        return ResponseEntity.ok(restaurantes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurante> buscarPorId(@PathVariable Long id){
        Restaurante restaurante = service.buscarPorId(id);
        return ResponseEntity.ok(restaurante);
    }
     @DeleteMapping("/{id}")
     public ResponseEntity<Void> excluir(@PathVariable Long id) {

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }

    

}
