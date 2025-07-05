package com.example.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_usuario;
    @Column(name = "nombre_usuario")
    private String nombreUsuario;
    private String pass_usuario;

    //Relacion muchos a uno con perfil
    @ManyToOne
    @JoinColumn(name = "id_perfil", referencedColumnName = "id_perfil")
    private Perfil perfil;

    //Relacion de muchos a uno con individuo

    @ManyToOne
    @JoinColumn(name = "id_individuo", referencedColumnName = "id_individuo")
    private Individuo individuo;
}