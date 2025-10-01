
package com.example.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "contratista")
public class Contratista implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Contratista")
    private Long idContratista;

    // Contacto principal (Persona), igual que Proveedor
    @ManyToOne
    @JoinColumn(name = "id_Persona", referencedColumnName = "id_Persona")
    private Persona idPersona;

    // Información comercial del contratista (empresa o natural)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_Info_Comerc", referencedColumnName = "id_Info_Comerc")
    private InformacionComercial informacionComercial;


}
