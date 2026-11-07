package com.smartpraia.smartpraia.restaurante.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestauranteRequestDTO {

    @NotBlank(message = "O nome do restaurante é obrigatório")
    @Size(max = 150, message = " O nome do restaurante deve conter no máximo 150 caracteres")
    private String nome;

    @NotBlank(message = "O CNPJ é obrigatório")
    @Pattern(  regexp = "[0-9]{14}",
        message = "O CNPJ deve conter exatamente 14 números"
    )
    private String cnpj;

    @NotBlank(message = "O e-mail é obrigatorio")
    @Email(message = "O e-mail é obrigatório")
    private String email;

    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracters")
    private String telefone;


}
