package com.example.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Entity
@Table(name = "presupuesto")


public class Presupuesto implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_obra")
    private Integer id_obra;

    @Column(name = "nombre", nullable = false)
    private String obraName;

    @Column(name = "productos", columnDefinition = "JSON")
    @Convert(converter = ViernesTrece.class)
    private Map<Integer, Double> activiValues;


}
