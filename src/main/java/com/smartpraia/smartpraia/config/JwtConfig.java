
package com.smartpraia.smartpraia.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.smartpraia.smartpraia.restaurante.repository.RestauranteRepository;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey secretKey(
        @Value("${api.security.token.secret}") String secret
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        return new NimbusJwtEncoder(
            new ImmutableSecret<>(secretKey)
        );
    }

    @Bean
    public JwtDecoder jwtDecoder(
        SecretKey secretKey,
        RestauranteRepository restauranteRepository
    ) {

        NimbusJwtDecoder decoder = NimbusJwtDecoder
            .withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build();

        decoder.setJwtValidator(jwt -> {

            String perfil = jwt.getClaimAsString("perfil");

            // O SUPER_ADMIN não está vinculado a um restaurante.
            if ("SUPER_ADMIN".equals(perfil)) {
                return OAuth2TokenValidatorResult.success();
            }

            Object claim = jwt.getClaims().get("restauranteId");

            if (!(claim instanceof Number)) {
                return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error(
                        "invalid_token",
                        "Token sem restaurante associado.",
                        null
                    )
                );
            }

            Long restauranteId = ((Number) claim).longValue();

            boolean restauranteAtivo = restauranteRepository
                .findById(restauranteId)
                .map(restaurante -> restaurante.isAtivo())
                .orElse(false);

            if (!restauranteAtivo) {
                return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error(
                        "invalid_token",
                        "Restaurante inativo ou não encontrado.",
                        null
                    )
                );
            }

            return OAuth2TokenValidatorResult.success();
        });

        return decoder;
    }
}
