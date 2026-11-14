

package com.smartpraia.smartpraia.usuario.vinculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartpraia.smartpraia.usuario.model.Usuario;
import com.smartpraia.smartpraia.usuario.repository.UsuarioRepository;
import com.smartpraia.smartpraia.usuario.vinculo.model.VinculoUsuario;
import com.smartpraia.smartpraia.usuario.vinculo.repository.VinculoUsuarioRepository;

@Service
public class MigracaoVinculoUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final VinculoUsuarioRepository vinculoUsuarioRepository;

    public MigracaoVinculoUsuarioService(
        UsuarioRepository usuarioRepository,
        VinculoUsuarioRepository vinculoUsuarioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.vinculoUsuarioRepository = vinculoUsuarioRepository;
    }

    @Transactional
    public void migrarVinculosExistentes() {

        List<Usuario> usuarios = usuarioRepository.findAll();

        for (Usuario usuario : usuarios) {

            if (usuario.getRestaurante() == null) {
                continue;
            }

            boolean vinculoExiste = vinculoUsuarioRepository
                .findByUsuario_IdAndRestaurante_Id(
                    usuario.getId(),
                    usuario.getRestaurante().getId()
                )
                .isPresent();

            if (vinculoExiste) {
                continue;
            }

            VinculoUsuario vinculo = new VinculoUsuario();

            vinculo.setUsuario(usuario);
            vinculo.setRestaurante(usuario.getRestaurante());
            vinculo.setPerfil(usuario.getPerfil());
            vinculo.setAtivo(usuario.getAtivo());

            vinculoUsuarioRepository.save(vinculo);
        }
    }
}
