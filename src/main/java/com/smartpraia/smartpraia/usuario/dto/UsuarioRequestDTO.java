package com.smartpraia.smartpraia.usuario.dto;

import com.smartpraia.smartpraia.usuario.model.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 150)
    String nome,

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Informe um e-mail válido")
    @Size(max = 150)
    String email,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
    String senha,

    @NotNull(message = "O perfil é obrigatório")
    PerfilUsuario perfil,

    Long restauranteId

) {}