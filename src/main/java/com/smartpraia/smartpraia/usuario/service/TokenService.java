
package com.smartpraia.smartpraia.usuario.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.smartpraia.smartpraia.usuario.model.Usuario;
import com.smartpraia.smartpraia.usuario.vinculo.model.VinculoUsuario;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;

    @Value("${api.security.token.expiration}")
    private long expiration;

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String gerarToken(
        Usuario usuario,
        VinculoUsuario vinculo
    ) {

        if (!vinculo.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException(
                "O vínculo não pertence ao usuário informado."
            );
        }

        if (!vinculo.isAtivo()
            || !vinculo.getRestaurante().isAtivo()) {
            throw new IllegalArgumentException(
                "Vínculo ou restaurante inativo."
            );
        }

        Instant agora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("smartpraia")
            .subject(usuario.getEmail())
            .issuedAt(agora)
            .expiresAt(agora.plusMillis(expiration))
            .claim("usuarioId", usuario.getId())
            .claim("vinculoId", vinculo.getId())
            .claim("restauranteId", vinculo.getRestaurante().getId())
            .claim("perfil", vinculo.getPerfil().name())
            .build();

        return gerarJwt(claims);
    }

    public String gerarTokenSuperAdmin(Usuario usuario) {

        Instant agora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("smartpraia")
            .subject(usuario.getEmail())
            .issuedAt(agora)
            .expiresAt(agora.plusMillis(expiration))
            .claim("usuarioId", usuario.getId())
            .claim("perfil", "SUPER_ADMIN")
            .build();

        return gerarJwt(claims);
    }

    private String gerarJwt(JwtClaimsSet claims) {

        JwsHeader header = JwsHeader
            .with(MacAlgorithm.HS256)
            .build();

        return jwtEncoder.encode(
            JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}
