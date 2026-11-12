package com.smartpraia.smartpraia.restaurante.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CadastroRestauranteDTO(
    
    @NotBlank(message = "O nome do Restaurante é obrigatório")
    @Size(max=150)
    String nomeRestaurante,

    @NotBlank(message = "O CNPJ é obrigatório")
    @Pattern(
        regexp = "[0-9]{14}",
        message = "O CNPJ deve conter 14 números"
    ) 
    String cnpj,

    @NotBlank(message="O e-mail do restaurante é obrigatório")
    @Email(message = "Informe um e-mail válido")
    String emailRestaurante,

    
    @Size(max=20)
    String telefone,
    
    @NotBlank(message="O nome do proprietário é obrigatório")
    @Size(max=150)
    String nomeProprietario,
    
    @NotBlank(message="O e-mail do proprietario é obrigatporio")
    @Email(message= "Informe um e-mail válido")
    @Size(max=150)
    String emailProprietario,
    
    @NotBlank(message="A senha é obrigatória")
     @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
     String senha
    ) {}
