package com.smartpraia.smartpraia.restaurante.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.smartpraia.smartpraia.usuario.model.Usuario;
import com.smartpraia.smartpraia.usuario.model.PerfilUsuario;
import com.smartpraia.smartpraia.usuario.repository.UsuarioRepository;
import com.smartpraia.smartpraia.shared.exception.EmailJaCadastradoException;
import com.smartpraia.smartpraia.restaurante.dto.CadastroRestauranteDTO;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.smartpraia.smartpraia.restaurante.model.Restaurante;
import com.smartpraia.smartpraia.restaurante.repository.RestauranteRepository;
import com.smartpraia.smartpraia.restaurante.dto.RestauranteRequestDTO;
import com.smartpraia.smartpraia.shared.exception.RecursoNaoEncontradoException;
import com.smartpraia.smartpraia.shared.exception.CnpjJaCadastradoException;
import com.smartpraia.smartpraia.shared.exception.RegraNegocioException;
import java.util.List;


@Service 
public class RestauranteService {

    private final UsuarioRepository usuarioRepository;
    private final RestauranteRepository repository;
    private final PasswordEncoder passwordEncoder;

    public RestauranteService(
        RestauranteRepository repository, 
        UsuarioRepository usuarioRepository, 
        PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Restaurante cadastrar(RestauranteRequestDTO dto){

        if(repository.existsByCnpj(dto.getCnpj())){
            throw new CnpjJaCadastradoException(
                "Ja existe Restaurante cadastrado com esse cnpj"
            );

        }
        Restaurante restaurante = new Restaurante();

        restaurante.setNome(dto.getNome());
        restaurante.setCnpj(dto.getCnpj());
        restaurante.setEmail(dto.getEmail());
        restaurante.setTelefone(dto.getTelefone());

        return repository.save(restaurante);

    }

   @Transactional
    public Restaurante cadastrarComProprietario(CadastroRestauranteDTO dto) {

        // Verifica se o CNPJ já está cadastrado
        if (repository.existsByCnpj(dto.cnpj())) {
            throw new CnpjJaCadastradoException(
                    "Já existe restaurante cadastrado com esse CNPJ."
            );
        }

        // Verifica se o e-mail do proprietário já está cadastrado
        if (usuarioRepository.existsByEmail(dto.emailProprietario())) {
            throw new EmailJaCadastradoException(
                    "Já existe usuário cadastrado com esse e-mail."
            );
        }

        // Cria o restaurante
        Restaurante restaurante = new Restaurante();

        restaurante.setNome(dto.nomeRestaurante());
        restaurante.setCnpj(dto.cnpj());
        restaurante.setEmail(dto.emailRestaurante());
        restaurante.setTelefone(dto.telefone());

        Restaurante restauranteSalvo = repository.save(restaurante);

        // Cria o primeiro proprietário
        Usuario proprietario = new Usuario();

        proprietario.setNome(dto.nomeProprietario());
        proprietario.setEmail(dto.emailProprietario());
        proprietario.setSenha(passwordEncoder.encode(dto.senha()));

        // Define o perfil automaticamente
        proprietario.setPerfil(PerfilUsuario.PROPRIETARIO);

        // Vincula o proprietário ao restaurante recém-criado
        proprietario.setRestaurante(restauranteSalvo);

        usuarioRepository.save(proprietario);

        return restauranteSalvo;
    }

    public List<Restaurante> listarTodos(Long restauranteId, String perfil){
        if(PerfilUsuario.SUPER_ADMIN.name().equals(perfil)){
            return repository.findAll();
        }

        if(!PerfilUsuario.PROPRIETARIO.name().equals(perfil)){
            throw new RegraNegocioException(
                "Você não tem permissão para consultar os restaurantes"
            );
        }

        if(restauranteId == null){
            throw new RegraNegocioException(
                "O usuário autenticado não está vinculado a um restaurante"
            );
        }

        return repository.findById(restauranteId)
        .map(List::of)
        .orElseThrow(()-> new RecursoNaoEncontradoException(
            "Restaurante não encotrado"
        ));
      

    }

    public Restaurante buscarPorId(Long id, Long restauranteIdToken, String perfil){
        if(PerfilUsuario.SUPER_ADMIN.name().equals(perfil)){
            return repository.findById(id)
             .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Restaurante não encontrado com o ID " + id
                ));
        }

            if(!PerfilUsuario.PROPRIETARIO.name().equals(perfil)){
                throw new RegraNegocioException(
                    "Você não tem permissão para consultar este restaurante"
                );
            }

            if(restauranteIdToken == null){
                throw new RegraNegocioException(
                    "O usuario autenticado não esta vinculado a um restaurante"
                );
            }

            if(!id.equals(restauranteIdToken)){
                throw new RecursoNaoEncontradoException(
                    "Restaurante não encontrado"
                );
            }
        
      return repository.findById(restauranteIdToken)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                    "Restaurante não encontrado."
            ));
}

    // public void excluir(Long id){
    //     Restaurante restaurante = buscarPorId(id);
    //     repository.delete(restaurante);
    // }

}
