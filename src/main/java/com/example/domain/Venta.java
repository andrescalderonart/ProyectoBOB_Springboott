package com.example.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
@Table (name = "venta")
public class Venta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_venta;

    private String fecha;
    private Double total;

    @ManyToOne
    @JoinColumn(name = "id_vendedor", referencedColumnName = "id_vendedor")
    private Vendedor vendedor;
}
