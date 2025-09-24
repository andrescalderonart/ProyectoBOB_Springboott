package com.example.domain;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idProveedor;                  // 5.1. id_Proveedor

    // Muchos proveedores pueden referenciar a una misma persona (si tu dominio lo permite).
    // Si en tu caso es 1:1, cambia a @OneToOne.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_persona", nullable = false)
    private Individuo individuo;                   // 1.1. id_Persona

    // Suelen ser datos exclusivos del proveedor → 1:1
    @Valid
    @NotNull
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_info_comerc", unique = true, nullable = false)
    private InformacionComercial informacionComercial; // 3.1. id_Info_Comerc*

    public Proveedor() {}

    public Proveedor(Individuo individuo, InformacionComercial info) {
        this.individuo = individuo;
        this.informacionComercial = info;
    }

    public Long getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Long idProveedor) { this.idProveedor = idProveedor; }

    public Individuo getIndividuo() { return individuo; }
    public void setIndividuo(Individuo individuo) { this.individuo = individuo; }

    public InformacionComercial getInformacionComercial() { return informacionComercial; }
    public void setInformacionComercial(InformacionComercial informacionComercial) {
        this.informacionComercial = informacionComercial;
    }
}
