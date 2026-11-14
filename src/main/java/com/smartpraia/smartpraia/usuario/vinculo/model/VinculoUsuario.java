package com.smartpraia.smartpraia.usuario.vinculo.model;

import java.time.LocalDateTime;

import com.smartpraia.smartpraia.restaurante.model.Restaurante;
import com.smartpraia.smartpraia.usuario.model.PerfilUsuario;
import com.smartpraia.smartpraia.usuario.model.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name="tb_vinculo_usuario",
    uniqueConstraints={
        @UniqueConstraint (
            name="uk_usuario_restaurante",
            columnNames={"usuario_id", "restaurante_id"}
        )
    }
) 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class VinculoUsuario {

    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="usuario_id", nullable=false)
    private Usuario usuario; 

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="restaurante_id", nullable=false)
    private Restaurante restaurante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=30)
    private PerfilUsuario perfil;

    @Column(nullable=false)
    private boolean ativo = true;

    @Column(nullable=false)
    private LocalDateTime dataVinculacao;

    @PrePersist
    public void prePersist(){
        if(dataVinculacao == null){
            dataVinculacao = LocalDateTime.now();
        }
    }

    
}
