package com.example.dao;

import com.example.domain.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorDao extends JpaRepository<Proveedor, Long> {
    boolean existsByInformacionComercial_NitRut(String nitRut);
    // Para permitir editar sin falso duplicado:
    boolean existsByInformacionComercial_NitRutAndIdProveedorNot(String nitRut, Long idProveedor);
}
