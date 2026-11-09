package com.smartpraia.smartpraia.usuario.service;

import java.util.List;

import  org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import  com.smartpraia.smartpraia.shared.exception.EmailJaCadastradoException;
import com.smartpraia.smartpraia.shared.exception.RecursoNaoEncontradoException;
import com.smartpraia.smartpraia.usuario.dto.UsuarioRequestDTO;
import com.smartpraia.smartpraia.usuario.model.Usuario;
import com.smartpraia.smartpraia.usuario.repository.UsuarioRepository;
import com.smartpraia.smartpraia.usuario.dto.UsuarioResponseDTO;


@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto){

        if(repository.existsByEmail(dto.email())){
            throw new EmailJaCadastradoException(
                "Ja existe usuário cadastrado com esse e-mail"
            );
        }

        Usuario usuario = new Usuario();
            usuario.setNome(dto.nome());
            usuario.setEmail(dto.email());

            usuario.setSenha(passwordEncoder.encode(dto.senha()));
            usuario.setPerfil(dto.perfil());

            Usuario usuarioSalvo = repository.save(usuario);

            return converterParaDTO(usuarioSalvo);

       
        
    }

    public List<UsuarioResponseDTO> listarTodos(){
        return repository.findAll()
        .stream()
        .map(this::converterParaDTO)
        .toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id){
        Usuario usuario = repository.findById(id)
        .orElseThrow(()-> new RecursoNaoEncontradoException(
            "Usuário com esse id não encontrado " + id
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
