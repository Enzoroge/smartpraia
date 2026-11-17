package com.smartpraia.smartpraia.usuario.service;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartpraia.smartpraia.usuario.dto.LoginRequestDTO;
import com.smartpraia.smartpraia.usuario.dto.LoginResponseDTO;
import com.smartpraia.smartpraia.usuario.dto.RestauranteOpcaoDTO;
import com.smartpraia.smartpraia.usuario.model.PerfilUsuario;
import com.smartpraia.smartpraia.usuario.model.Usuario;
import com.smartpraia.smartpraia.usuario.repository.UsuarioRepository;
import com.smartpraia.smartpraia.usuario.vinculo.model.VinculoUsuario;
import com.smartpraia.smartpraia.usuario.vinculo.repository.VinculoUsuarioRepository;

@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final VinculoUsuarioRepository vinculoUsuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AutenticacaoService(
        UsuarioRepository usuarioRepository,
        VinculoUsuarioRepository vinculoUsuarioRepository,
        PasswordEncoder passwordEncoder,
        TokenService tokenService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.vinculoUsuarioRepository = vinculoUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {

        Usuario usuario = usuarioRepository.findByEmail(dto.email())
            .orElseThrow(() -> new BadCredentialsException(
                "E-mail ou senha inválidos."
            ));

        if (!Boolean.TRUE.equals(usuario.getAtivo())
            || !passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new BadCredentialsException(
                "E-mail ou senha inválidos."
            );
        }

        // Acesso global do SUPER_ADMIN.
        if (usuario.getPerfil() == PerfilUsuario.SUPER_ADMIN) {

            if (dto.vinculoId() != null) {
                throw new BadCredentialsException(
                    "Administrador global não seleciona restaurante."
                );
            }

            String token = tokenService.gerarTokenSuperAdmin(usuario);

            return new LoginResponseDTO(
                token,
                "Bearer",
                false,
                List.of()
            );
        }

        // Busca os vínculos ativos do usuário.
        List<VinculoUsuario> vinculos =
            vinculoUsuarioRepository.findAllByUsuario_IdAndAtivoTrue(
                usuario.getId()
            );

        // Remove vínculos cujo restaurante esteja inativo.
        vinculos = vinculos.stream()
            .filter(vinculo -> vinculo.getRestaurante().isAtivo())
            .toList();

        if (vinculos.isEmpty()) {
            throw new BadCredentialsException(
                "Usuário não possui restaurante ativo."
            );
        }

        // Se o usuário ainda não escolheu um restaurante.
        if (dto.vinculoId() == null) {

            // Se possui apenas um vínculo, entra diretamente.
            if (vinculos.size() == 1) {

                String token = tokenService.gerarToken(
                    usuario,
                    vinculos.get(0)
                );

                return new LoginResponseDTO(
                    token,
                    "Bearer",
                    false,
                    List.of()
                );
            }

            // Se possui vários vínculos, devolve as opções.
            List<RestauranteOpcaoDTO> restaurantes = vinculos.stream()
                .map(vinculo -> new RestauranteOpcaoDTO(
                    vinculo.getId(),
                    vinculo.getRestaurante().getId(),
                    vinculo.getRestaurante().getNome(),
                    vinculo.getPerfil().name()
                ))
                .toList();

            return new LoginResponseDTO(
                null,
                null,
                true,
                restaurantes
            );
        }

        // O usuário escolheu um vínculo específico.
        VinculoUsuario vinculo = vinculoUsuarioRepository
            .findByIdAndUsuario_Id(
                dto.vinculoId(),
                usuario.getId()
            )
            .orElseThrow(() -> new BadCredentialsException(
                "Vínculo não encontrado para este usuário."
            ));

        if (!vinculo.isAtivo()
            || !vinculo.getRestaurante().isAtivo()) {
            throw new BadCredentialsException(
                "Vínculo ou restaurante inativo."
            );
        }

        String token = tokenService.gerarToken(usuario, vinculo);

        return new LoginResponseDTO(
            token,
            "Bearer",
            false,
            List.of()
        );
    }
}