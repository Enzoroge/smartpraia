
package com.smartpraia.smartpraia.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.smartpraia.smartpraia.usuario.model.PerfilUsuario;
import com.smartpraia.smartpraia.usuario.repository.UsuarioRepository;
import com.smartpraia.smartpraia.usuario.vinculo.model.VinculoUsuario;
import com.smartpraia.smartpraia.usuario.vinculo.repository.VinculoUsuarioRepository;

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
        UsuarioRepository usuarioRepository,
        VinculoUsuarioRepository vinculoUsuarioRepository
    ) {

        NimbusJwtDecoder decoder = NimbusJwtDecoder
            .withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build();

        OAuth2TokenValidator<Jwt> validadorPersonalizado = jwt -> {

            String perfil = jwt.getClaimAsString("perfil");

            Object usuarioClaim = jwt.getClaims().get("usuarioId");

            if (!(usuarioClaim instanceof Number)) {
                return falha("Token sem usuário autenticado.");
            }

            Long usuarioId = ((Number) usuarioClaim).longValue();

            var usuarioOptional = usuarioRepository.findById(usuarioId);

            if (usuarioOptional.isEmpty()
                || !Boolean.TRUE.equals(usuarioOptional.get().getAtivo())) {
                return falha("Usuário inativo ou não encontrado.");
            }

            var usuario = usuarioOptional.get();

            // SUPER_ADMIN possui acesso global e não precisa de vínculo.
            if ("SUPER_ADMIN".equals(perfil)) {

                if (usuario.getPerfil() != PerfilUsuario.SUPER_ADMIN) {
                    return falha("Perfil de administrador inválido.");
                }

                if (jwt.getClaims().containsKey("vinculoId")
                    || jwt.getClaims().containsKey("restauranteId")) {
                    return falha("Token de administrador contém vínculo indevido.");
                }

                return OAuth2TokenValidatorResult.success();
            }

            Object vinculoClaim = jwt.getClaims().get("vinculoId");
            Object restauranteClaim = jwt.getClaims().get("restauranteId");

            if (!(vinculoClaim instanceof Number)
                || !(restauranteClaim instanceof Number)
                || perfil == null) {
                return falha("Token sem vínculo, restaurante ou perfil.");
            }

            Long vinculoId = ((Number) vinculoClaim).longValue();
            Long restauranteId = ((Number) restauranteClaim).longValue();

            var vinculoOptional = vinculoUsuarioRepository
                .findByIdAndUsuario_Id(vinculoId, usuarioId);

            if (vinculoOptional.isEmpty()) {
                return falha("Vínculo não encontrado para este usuário.");
            }

            VinculoUsuario vinculo = vinculoOptional.get();

            if (!vinculo.isAtivo()) {
                return falha("Vínculo do usuário está inativo.");
            }

            if (!vinculo.getRestaurante().isAtivo()) {
                return falha("Restaurante inativo.");
            }

            if (!vinculo.getRestaurante().getId().equals(restauranteId)) {
                return falha("Restaurante não corresponde ao vínculo.");
            }

            if (!vinculo.getPerfil().name().equals(perfil)) {
                return falha("Perfil não corresponde ao vínculo.");
            }

            return OAuth2TokenValidatorResult.success();
        };

        // Mantém também as validações padrão, como expiração do JWT.
        OAuth2TokenValidator<Jwt> validadorCompleto =
            new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(),
                validadorPersonalizado
            );

        decoder.setJwtValidator(validadorCompleto);

        return decoder;
    }

    private OAuth2TokenValidatorResult falha(String mensagem) {
        return OAuth2TokenValidatorResult.failure(
            new OAuth2Error(
                "invalid_token",
                mensagem,
                null
            )
        );
    }
}
