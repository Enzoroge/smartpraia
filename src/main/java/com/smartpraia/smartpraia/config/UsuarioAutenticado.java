package com.smartpraia.smartpraia.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAutenticado {

    private Long getUsuarioId(){
        return obterClaimLong("usuarioId");
    }

    public Long getVinculoId(){
        return obterClaimLong("vinculoId");
    }

    public Long getRestauranteId(){
        return obterClaimLong("restauranteId");
    }

    public String getPerfil(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof JwtAuthenticationToken jwtAuth)){
            throw new IllegalStateException("Usuário não autenticado pr JWT>");
        }

        return jwtAuth.getToken().getClaimAsString("perfil");
    }

    private Long obterClaimLong(String nomeClaim){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

         if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            throw new IllegalStateException("Usuário não autenticado por JWT.");
        }

        return jwtAuth.getToken().getClaim(nomeClaim);
    }
}
