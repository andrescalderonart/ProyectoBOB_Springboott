package com.example.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Entity
@Table(name = "avance")


public class Avance implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avance")
    private Integer id_avance;

    @Column(name = "id_usuario", nullable = false)
    private Integer id_usuario;

    @Column(name = "id_obra", nullable = false)
    private Integer id_obra;

    @Column(name = "fecha")
    private String fecha;

    @Column(name = "id_matriz", nullable = false)
    private Integer id_matriz;

    @Column(name = "cantidad", nullable = false)
    private Double cantidad;

}
