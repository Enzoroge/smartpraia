package com.smartpraia.smartpraia.usuario.service;

import org.springframework.stereotype.Service;

import  com.smartpraia.smartpraia.usuario.repository.UsuarioRepository;
import com.smartpraia.smartpraia.shared.exception.RecursoNaoEncontradoException;
import  com.smartpraia.smartpraia.usuario.model.Usuario;
import com.smartpraia.smartpraia.shared.exception.EmailJaCadastradoException;
import com.smartpraia.smartpraia.usuario.dto.UsuarioRequestDTO;



import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository){
        this.repository = repository;
    }

    public Usuario cadastrar(UsuarioRequestDTO dto){

        if(repository.existsByEmail(dto.email())){
            throw new EmailJaCadastradoException(
                "Ja existe usuário cadastrado com esse e-mail"
            );
        }

        Usuario usuario = new Usuario();
            usuario.setNome(dto.nome());
            usuario.setEmail(dto.email());
            usuario.setSenha(dto.senha());
            usuario.setPerfil(dto.perfil());

            return repository.save(usuario);
        
    }

    public List<Usuario> listarTodos(){
        return repository.findAll();
    }

    public Usuario buscarPorId(Long id){
        return repository.findById(id)
        .orElseThrow(()-> new RecursoNaoEncontradoException(
            "Usuário com esse id não encontrado " + id
        ));
    }

}
