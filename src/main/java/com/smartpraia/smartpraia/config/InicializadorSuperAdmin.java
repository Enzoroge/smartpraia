package com.smartpraia.smartpraia.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.smartpraia.smartpraia.usuario.model.PerfilUsuario;
import com.smartpraia.smartpraia.usuario.model.Usuario;
import com.smartpraia.smartpraia.usuario.repository.UsuarioRepository;

@Configuration
public class InicializadorSuperAdmin {

    @Bean
    CommandLineRunner criarSuperAdmin(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder
    ){
        return args -> {
            String email = "superadmin@smartpraia.com";

            if(usuarioRepository.findByEmail(email).isEmpty()){
                Usuario adm = new Usuario();

                adm.setNome("Administrador SmartPraia");
                adm.setEmail(email);
                adm.setSenha(passwordEncoder.encode("adm12345"));
                adm.setPerfil(PerfilUsuario.SUPER_ADMIN);
                adm.setAtivo(true);
                adm.setRestaurante(null);

                usuarioRepository.save(adm);
            }
        };
    }

}
