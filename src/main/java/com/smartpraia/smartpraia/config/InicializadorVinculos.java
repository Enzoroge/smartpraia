
package com.smartpraia.smartpraia.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartpraia.smartpraia.usuario.vinculo.service.MigracaoVinculoUsuarioService;

@Configuration
public class InicializadorVinculos {

    @Bean
    CommandLineRunner migrarVinculos(
        MigracaoVinculoUsuarioService migracaoService
    ) {
        return args -> {
            migracaoService.migrarVinculosExistentes();
        };
    }
}
