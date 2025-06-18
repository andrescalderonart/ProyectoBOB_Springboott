package com.example.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "inventario")
public class Inventario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_inventario;

    @NotEmpty
    @Column(name = "nombre_gestor")
    private String nombreGestor;

    @NotEmpty
    @Column(name = "nombre_obra")
    private String nombreobra;

    @NotEmpty
    private String tipoRegistro;

    @NotNull
    private LocalDate fecha;

    @NotNull
    @Positive
    private Integer cantidad;

    @NotEmpty
    private String material;

    @NotNull
    @Positive
    @Column(name = "precio_unidad")
    private Double precioUnidad;
}
