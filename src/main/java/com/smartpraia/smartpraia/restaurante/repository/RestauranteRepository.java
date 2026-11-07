package com.smartpraia.smartpraia.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartpraia.smartpraia.restaurante.model.Restaurante;


public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

}
