package com.smartpraia.smartpraia.usuario.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartpraia.smartpraia.usuario.dto.LoginRequestDTO;
import com.smartpraia.smartpraia.usuario.dto.LoginResponseDTO;
import com.smartpraia.smartpraia.usuario.model.Usuario;
import com.smartpraia.smartpraia.usuario.repository.UsuarioRepository;

@Service 
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AutenticacaoService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        TokenService tokenService
    ){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {

        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BadCredentialsException(
                        "E-mail ou senha inválidos."));

        if (!usuario.getAtivo()) {
            throw new BadCredentialsException(
                    "E-mail ou senha inválidos.");
        }

        if(usuario.getRestaurante() != null && !usuario.getRestaurante().isAtivo()){
          throw new BadCredentialsException(
                "E-mail ou senha inválido"
            );
        }

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new BadCredentialsException(
                    "E-mail ou senha inválidos.");
        }

        String token = tokenService.gerarToken(usuario);

        return new LoginResponseDTO(token, "Bearer");
    }
}


