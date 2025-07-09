package com.example.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "matriz")


public class Matriz implements Serializable{

    @Id
    @Column(name = "id_matriz")
    private Integer id_m;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "unidades")
    private String unidades;


}