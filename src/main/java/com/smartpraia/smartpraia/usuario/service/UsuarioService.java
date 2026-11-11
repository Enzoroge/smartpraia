package com.smartpraia.smartpraia.usuario.service;

import java.util.List;

import  org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartpraia.smartpraia.restaurante.model.Restaurante;
import com.smartpraia.smartpraia.restaurante.repository.RestauranteRepository;
import  com.smartpraia.smartpraia.shared.exception.EmailJaCadastradoException;
import com.smartpraia.smartpraia.shared.exception.RecursoNaoEncontradoException;
import com.smartpraia.smartpraia.shared.exception.RegraNegocioException;
import com.smartpraia.smartpraia.usuario.dto.UsuarioRequestDTO;
import com.smartpraia.smartpraia.usuario.model.Usuario;
import com.smartpraia.smartpraia.usuario.model.PerfilUsuario;
import com.smartpraia.smartpraia.usuario.repository.UsuarioRepository;
import com.smartpraia.smartpraia.usuario.dto.UsuarioResponseDTO;


@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RestauranteRepository restauranteRepository;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder, RestauranteRepository restauranteRepository ){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.restauranteRepository = restauranteRepository;
    }

    
public UsuarioResponseDTO cadastrar(
        UsuarioRequestDTO dto,
        Long restauranteIdToken) {

    // Verifica se o e-mail já está cadastrado
    if (repository.existsByEmail(dto.email())) {
        throw new EmailJaCadastradoException(
                "Já existe usuário cadastrado com esse e-mail."
        );
    }

    // Impede a criação de SUPER_ADMIN por esta operação
    if (dto.perfil() == PerfilUsuario.SUPER_ADMIN) {
        throw new RegraNegocioException(
                "Não é permitido cadastrar SUPER_ADMIN por esta operação."
        );
    }

    // Verifica se o usuário autenticado possui restaurante
    if (restauranteIdToken == null) {
        throw new RegraNegocioException(
                "O usuário autenticado não está vinculado a um restaurante."
        );
    }

    // Busca o restaurante identificado pelo JWT
    Restaurante restaurante = restauranteRepository
            .findById(restauranteIdToken)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                    "Restaurante não encontrado com ID: " + restauranteIdToken
            ));

    // Cria o usuário
    Usuario usuario = new Usuario();

    usuario.setNome(dto.nome());
    usuario.setEmail(dto.email());
    usuario.setSenha(passwordEncoder.encode(dto.senha()));
    usuario.setPerfil(dto.perfil());
    usuario.setRestaurante(restaurante);

    Usuario usuarioSalvo = repository.save(usuario);

    return converterParaDTO(usuarioSalvo);
}


    public List<UsuarioResponseDTO> listarTodos(Long restauranteIdToken){
        if(restauranteIdToken == null){
            throw new RegraNegocioException(
                "O usuário autenticado não esta vinculado a um restaurante"
            );
        }

        return repository.findAllByRestaurante_Id(restauranteIdToken)
        .stream()
        .map(this::converterParaDTO)
        .toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id, Long restauranteIdToken){
       if(restauranteIdToken == null){
        throw new RegraNegocioException(
            "O usuário autenticado não está vinculado a um restaurante"
        );

       }
       Usuario usuario = repository.findByIdAndRestaurante_Id(id, restauranteIdToken)
       .orElseThrow(() -> new RecursoNaoEncontradoException(
        "Usuário não encontrado"
       ));
       return converterParaDTO(usuario);
    }

    private UsuarioResponseDTO converterParaDTO(Usuario usuario){

        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getPerfil(),
            usuario.getAtivo(),
            usuario.getDataCadastro(),
            usuario.getRestaurante() != null ? usuario.getRestaurante().getId():null
        );
    }

}
