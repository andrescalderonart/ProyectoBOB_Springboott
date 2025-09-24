package com.example.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "informacion_comercial")
public class InformacionComercial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_info_comerc")
    private Long idInfoComerc;                 // 3.1. id_Info_Comerc

    @NotBlank
    @Size(max = 30)
    @Column(name = "nit_rut", updatable = false, nullable = false, length = 30)
    private String nitRut;                     // 3.2. NIT_RUT

    @NotBlank
    @Size(max = 30)
    @Column(name = "forma_pago", nullable = false, length = 30)
    private String formaPago;                  // 3.3. forma_Pago

    @NotBlank
    @Size(max = 60)
    @Column(name = "banco", updatable = false,nullable = false, length = 60)
    private String banco;                      // 3.4. banco

    @NotBlank
    @Size(max = 40)
    @Pattern(regexp = "^[0-9\\- ]+$", message = "Solo números, guiones o espacios.")
    @Column(name = "num_cuenta", updatable = false, nullable = false, length = 40)
    private String numCuenta;                  // 3.5. num_Cuenta

    @NotBlank
    @Size(max = 120)
    @Column(name = "direccion", nullable = false, length = 120)
    private String direccion;                  // 3.6. direccion

    public InformacionComercial() {}

    public InformacionComercial(String nitRut, String formaPago, String banco,
                                String numCuenta, String direccion) {
        this.nitRut = nitRut;
        this.formaPago = formaPago;
        this.banco = banco;
        this.numCuenta = numCuenta;
        this.direccion = direccion;
    }

    public Long getIdInfoComerc() { return idInfoComerc; }
    public void setIdInfoComerc(Long idInfoComerc) { this.idInfoComerc = idInfoComerc; }

    public String getNitRut() { return nitRut; }
    public void setNitRut(String nitRut) { this.nitRut = nitRut; }

    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public String getNumCuenta() { return numCuenta; }
    public void setNumCuenta(String numCuenta) { this.numCuenta = numCuenta; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
