package com.example.domain;

import jakarta.persistence.*;


@Entity
@Table(name = "codigobarras")
public class CodigoBarras{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_codigo;

    @OneToOne
    @JoinColumn(name = "id_producto", referencedColumnName = "id_producto")
    private Producto producto;

}
