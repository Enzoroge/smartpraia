package com.smartpraia.smartpraia.usuario.dto;

import com.smartpraia.smartpraia.usuario.model.PerfilUsuario;
import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        PerfilUsuario perfil,
        Boolean ativo,
        LocalDateTime dataCadastro,
        Long restauranteId
) {}