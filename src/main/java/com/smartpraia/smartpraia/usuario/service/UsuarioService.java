
package com.smartpraia.smartpraia.usuario.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartpraia.smartpraia.restaurante.model.Restaurante;
import com.smartpraia.smartpraia.restaurante.repository.RestauranteRepository;
import com.smartpraia.smartpraia.shared.exception.EmailJaCadastradoException;
import com.smartpraia.smartpraia.shared.exception.RecursoNaoEncontradoException;
import com.smartpraia.smartpraia.shared.exception.RegraNegocioException;
import com.smartpraia.smartpraia.usuario.dto.UsuarioRequestDTO;
import com.smartpraia.smartpraia.usuario.dto.UsuarioResponseDTO;
import com.smartpraia.smartpraia.usuario.model.PerfilUsuario;
import com.smartpraia.smartpraia.usuario.model.Usuario;
import com.smartpraia.smartpraia.usuario.repository.UsuarioRepository;
import com.smartpraia.smartpraia.usuario.vinculo.model.VinculoUsuario;
import com.smartpraia.smartpraia.usuario.vinculo.repository.VinculoUsuarioRepository;
import com.smartpraia.smartpraia.shared.exception.AcessoNegadoException;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RestauranteRepository restauranteRepository;
    private final VinculoUsuarioRepository vinculoRepository;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            RestauranteRepository restauranteRepository,
            VinculoUsuarioRepository vinculoRepository) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.restauranteRepository = restauranteRepository;
        this.vinculoRepository = vinculoRepository;
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(
            UsuarioRequestDTO dto,
            Long restauranteIdToken,
            String perfilUsuarioLogado
        
        ) {

        if (restauranteIdToken == null) {
            throw new RegraNegocioException(
                    "O usuário autenticado não está vinculado a um restaurante."
            );
        }

       if (perfilUsuarioLogado == null) {
    throw new RegraNegocioException(
        "Não foi possível identificar o perfil do usuário autenticado."
    );
}

        boolean permitido = switch (perfilUsuarioLogado) {

        case "PROPRIETARIO" ->
                dto.perfil() == PerfilUsuario.GERENTE ||
                dto.perfil() == PerfilUsuario.GARCOM ||
                dto.perfil() == PerfilUsuario.COZINHA ||
                dto.perfil() == PerfilUsuario.CAIXA;

        case "GERENTE" ->
                dto.perfil() == PerfilUsuario.GARCOM ||
                dto.perfil() == PerfilUsuario.COZINHA ||
                dto.perfil() == PerfilUsuario.CAIXA;

        default -> false;
};

      if(!permitido){
        throw new AcessoNegadoException(
                "Você não possui permissão para cadastrar este perfil de usuário."
        );
        }

        if (dto.perfil() == PerfilUsuario.SUPER_ADMIN) {
            throw new RegraNegocioException(
                    "Não é permitido cadastrar SUPER_ADMIN por esta operação."
            );
        }

        if (repository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException(
                    "Já existe uma conta com esse e-mail. Para associá-la a outro restaurante, será necessário um convite."
            );
        }

        Restaurante restaurante = restauranteRepository
                .findById(restauranteIdToken)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Restaurante não encontrado."
                ));

        if (!restaurante.isAtivo()) {
            throw new RegraNegocioException(
                    "Não é possível cadastrar usuários em um restaurante inativo."
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));

        // Campos antigos mantidos temporariamente
        // para compatibilidade com partes ainda não migradas.
        usuario.setPerfil(dto.perfil());
        usuario.setRestaurante(restaurante);

        Usuario usuarioSalvo = repository.save(usuario);

        VinculoUsuario vinculo = new VinculoUsuario();
        vinculo.setUsuario(usuarioSalvo);
        vinculo.setRestaurante(restaurante);
        vinculo.setPerfil(dto.perfil());
        vinculo.setAtivo(true);

        vinculoRepository.save(vinculo);

        return converterParaDTO(usuarioSalvo, vinculo);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos(Long restauranteIdToken) {

        if (restauranteIdToken == null) {
            throw new RegraNegocioException(
                    "O usuário autenticado não está vinculado a um restaurante."
            );
        }

        return vinculoRepository
                .findAllByRestaurante_IdAndAtivoTrue(restauranteIdToken)
                .stream()
                .filter(vinculo -> Boolean.TRUE.equals(
                        vinculo.getUsuario().getAtivo()
                ))
                .map(vinculo -> converterParaDTO(
                        vinculo.getUsuario(), vinculo
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(
            Long id,
            Long restauranteIdToken) {

        if (restauranteIdToken == null) {
            throw new RegraNegocioException(
                    "O usuário autenticado não está vinculado a um restaurante."
            );
        }

        VinculoUsuario vinculo = vinculoRepository
                .findByUsuario_IdAndRestaurante_IdAndAtivoTrue(
                        id, restauranteIdToken
                )
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário não encontrado neste restaurante."
                ));

        if (!Boolean.TRUE.equals(vinculo.getUsuario().getAtivo())) {
            throw new RecursoNaoEncontradoException(
                    "Usuário não encontrado neste restaurante."
            );
        }

        return converterParaDTO(vinculo.getUsuario(), vinculo);
    }

    private UsuarioResponseDTO converterParaDTO(
            Usuario usuario,
            VinculoUsuario vinculo) {

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                vinculo.getPerfil(),
                vinculo.isAtivo() && Boolean.TRUE.equals(usuario.getAtivo()),
                usuario.getDataCadastro(),
                vinculo.getRestaurante().getId()
        );
    }
}
