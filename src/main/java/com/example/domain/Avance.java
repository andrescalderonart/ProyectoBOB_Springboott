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
    private Integer idAvance;

    @Column(name = "id_usuario", nullable = false)
    private String idUsuario;

    @Column(name = "id_obra", nullable = false)
    private Integer idObra;

    @Column(name = "fecha")
    private String fecha;

    @Column(name = "id_matriz", nullable = false)
    private Integer idMatriz;

    @Column(name = "cantidad", nullable = false)
    private Double cantidad;

}