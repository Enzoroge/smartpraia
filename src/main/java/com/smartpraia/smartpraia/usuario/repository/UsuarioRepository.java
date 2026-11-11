package com.smartpraia.smartpraia.usuario.repository;

import com.smartpraia.smartpraia.usuario.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findAllByRestaurante_Id(Long restauranteId);

    Optional<Usuario> findByIdAndRestaurante_Id(Long id, Long restauranteId);

}
