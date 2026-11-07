package com.smartpraia.smartpraia.restaurante.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import jakarta.persistence.PrePersist;


@Entity
@Table (name="tb_restaurantes")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=150)
    private String nome;

    @Column(nullable=false, length=18, unique=true)
    private String cnpj;

    @Column(nullable=false, length=150)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(nullable=false)
    private boolean ativo = true;

    @Column(nullable=false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @PrePersist
    public void prePersist(){
        if(dataCadastro == null){
            dataCadastro = LocalDateTime.now();
        }
    }

}
