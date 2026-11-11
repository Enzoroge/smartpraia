package com.smartpraia.smartpraia.usuario.controller;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smartpraia.smartpraia.usuario.service.UsuarioService;
import com.smartpraia.smartpraia.usuario.model.Usuario;
import com.smartpraia.smartpraia.usuario.dto.UsuarioRequestDTO;
import com.smartpraia.smartpraia.usuario.dto.UsuarioResponseDTO;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
// @RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service){
        this.service = service;
    }

    @PostMapping("/usuario")
        public ResponseEntity<UsuarioResponseDTO> cadastrar(
                @Valid @RequestBody UsuarioRequestDTO dto,
                @AuthenticationPrincipal Jwt jwt) {

            Number restauranteIdToken = jwt.getClaim("restauranteId");

            Long restauranteId = restauranteIdToken != null
                    ? restauranteIdToken.longValue()
                    : null;

            UsuarioResponseDTO resposta = service.cadastrar(
                    dto,
                    restauranteId
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
}

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(@AuthenticationPrincipal Jwt jwt){
        Number restauranteIdToken = jwt.getClaim("restauranteId");

        Long restauranteId = restauranteIdToken != null
        ? restauranteIdToken.longValue():null;
        return ResponseEntity.ok(service.listarTodos(restauranteId));
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt){

        Number restauranteIdToken = jwt.getClaim("restauranteId");

        Long restauranteId = restauranteIdToken != null ? restauranteIdToken.longValue():null;
       

        return ResponseEntity.ok(service.buscarPorId(id, restauranteId));
    }

}
