package com.smartpraia.smartpraia.restaurante.controller;
import java.util.List;

import com.smartpraia.smartpraia.restaurante.dto.CadastroRestauranteDTO;

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

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.http.ResponseEntity;

@RestController
// @RequestMapping("/restaurantes")
public class RestauranteController {

    private final RestauranteService service;

    public RestauranteController(RestauranteService service){
        this.service = service;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Restaurante> cadastrarComProprietario(@Valid @RequestBody CadastroRestauranteDTO dto){
        Restaurante restaurante = service.cadastrarComProprietario(dto);

        return ResponseEntity.status(201).body(restaurante);
    }

    @PostMapping("/restaurante")
    public ResponseEntity<Restaurante> cadastrar(@Valid @RequestBody RestauranteRequestDTO dto){
        Restaurante novoRestaurante = service.cadastrar(dto);
        return ResponseEntity.status(201).body(novoRestaurante);
    }

    
    @GetMapping("/restaurantes")
    public ResponseEntity<List<Restaurante>> listarTodos(@AuthenticationPrincipal Jwt jwt){

        String perfil = jwt.getClaimAsString("perfil");

        Number restauranteIdToken = jwt.getClaim("restauranteId");

        Long restauranteId = restauranteIdToken != null
        ? restauranteIdToken.longValue() : null;

       List<Restaurante> restaurantes = service.listarTodos(
            restauranteId,
            perfil
    );
        return ResponseEntity.ok(restaurantes);
    }

    @GetMapping("/restaurante/{id}")
    public ResponseEntity<Restaurante> buscarPorId(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt){

        String perfil = jwt.getClaimAsString("perfil");
        Number restauranteIdToken = jwt.getClaim("restauranteId");

        Long restauranteId = restauranteIdToken != null
        ? restauranteIdToken.longValue(): null;

        Restaurante restaurante = service.buscarPorId(id, restauranteId, perfil);
        return ResponseEntity.ok(restaurante);
    }
    //  @DeleteMapping("/restaurante/{id}")
    //  public ResponseEntity<Void> excluir(@PathVariable Long id) {

    //     service.excluir(id);

    //     return ResponseEntity.noContent().build();
    // }

    

}
