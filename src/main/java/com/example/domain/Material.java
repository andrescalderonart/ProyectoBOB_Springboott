package com.example.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Entity
@Table(name = "matriz")


public class Material implements Serializable{

    @Id
    @Column(name = "id_matriz")
    private Integer id_m;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "unidades")
    private String unidades;


}
