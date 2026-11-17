package com.smartpraia.smartpraia.usuario.dto;

import java.util.List;

public record LoginResponseDTO(
    String token,
    String tipo,
    boolean selecionarRestaurante,
    List<RestauranteOpcaoDTO> restaurantes
) {

}
