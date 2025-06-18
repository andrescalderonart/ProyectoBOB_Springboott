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
    private Integer entryId;

    @Column(name = "nombre", nullable = false)
    private String entryName;

    @Column(name = "products", columnDefinition = "JSON")
    @Convert(converter = ViernesTrece.class)
    private Map<Integer, Double> materialValues;


}
