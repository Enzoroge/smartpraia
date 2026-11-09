package com.smartpraia.smartpraia.usuario.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import com.smartpraia.smartpraia.usuario.model.Usuario;
import java.time.Instant;



@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;

    // @Value("${api.security.token.secret}")
    // private String secret;

    @Value("${api.security.token.expiration}")
    private long expiration;

    public TokenService(JwtEncoder jwtEncoder){
        this.jwtEncoder = jwtEncoder;
    }

    public String gerarToken(Usuario usuario){

        Instant agora = Instant.now();

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
        .issuer("smartpraia")
        .subject(usuario.getEmail())
        .issuedAt(agora)
        .expiresAt(agora.plusMillis(expiration))
        .claim("perfil", usuario.getPerfil().name());

        if(usuario.getRestaurante() != null){
            claims.claim(
                "restauranteId",
                usuario.getRestaurante().getId()
            );
        }

        JwsHeader header = JwsHeader
        .with(MacAlgorithm.HS256)
        .build();

        return jwtEncoder.encode(
            JwtEncoderParameters.from(header, claims.build())
        ).getTokenValue();
    }

}
