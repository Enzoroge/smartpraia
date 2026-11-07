package com.smartpraia.smartpraia.restaurante.service;

import org.springframework.stereotype.Service;

import com.smartpraia.smartpraia.restaurante.model.Restaurante;
import com.smartpraia.smartpraia.restaurante.repository.RestauranteRepository;
import com.smartpraia.smartpraia.restaurante.dto.RestauranteRequestDTO;
import com.smartpraia.smartpraia.shared.exception.RecursoNaoEncontradoException;
import java.util.List;


@Service 
public class RestauranteService {
    private final RestauranteRepository repository;

    public RestauranteService(RestauranteRepository repository) {
        this.repository = repository;
    }

    public Restaurante cadastrar(RestauranteRequestDTO dto){
        Restaurante restaurante = new Restaurante();

        restaurante.setNome(dto.getNome());
        restaurante.setCnpj(dto.getCnpj());
        restaurante.setEmail(dto.getEmail());
        restaurante.setTelefone(dto.getTelefone());

        return repository.save(restaurante);

    }

    public List<Restaurante> listarTodos(){
        return repository.findAll();

    }

    public Restaurante buscarPorId(Long id){
        return repository.findById(id)
        .orElseThrow(()-> new RecursoNaoEncontradoException("Restaurante não encontrado com o Id " + id));
    }

    public void excluir(Long id){
        Restaurante restaurante = buscarPorId(id);
        repository.delete(restaurante);
    }

}
