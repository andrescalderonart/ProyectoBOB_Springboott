package com.example.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table (name = "perfil")
public class Perfil implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_perfil;
    private String descripcion_perfil;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "perfil_permiso",
            joinColumns = @JoinColumn(name = "id_perfil"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private List<Permiso> permisos;

    public void setPermiso(List<Permiso> permisos) {
        this.permisos = permisos;
    }
}
