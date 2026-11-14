
package com.smartpraia.smartpraia.usuario.vinculo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartpraia.smartpraia.usuario.vinculo.model.VinculoUsuario;

public interface VinculoUsuarioRepository
        extends JpaRepository<VinculoUsuario, Long> {

    List<VinculoUsuario> findAllByUsuario_Id(Long usuarioId);

    List<VinculoUsuario> findAllByUsuario_IdAndAtivoTrue(Long usuarioId);

    Optional<VinculoUsuario> findByUsuario_IdAndRestaurante_Id(
        Long usuarioId,
        Long restauranteId
    );

    Optional<VinculoUsuario> findByIdAndUsuario_Id(
        Long id,
        Long usuarioId
    );
}
