package com.example.servicio;

import com.example.dao.IndividuoDao;
import com.example.dao.ProveedorDao;
import com.example.domain.Proveedor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorServicioImp implements ProveedorServicio {

    private final ProveedorDao proveedorDao;
    private final IndividuoDao individuoDao;

    public ProveedorServicioImp(ProveedorDao proveedorDao, IndividuoDao individuoDao) {
        this.proveedorDao = proveedorDao;
        this.individuoDao = individuoDao;
    }

    @Override
    public List<Proveedor> listar() {
        return proveedorDao.findAll();
    }

    @Override
    public Optional<Proveedor> buscarPorId(Long id) {
        return proveedorDao.findById(id);
    }

    @Override
    @Transactional
    public Proveedor guardar(Proveedor proveedor) {

        // Resolver Individuo
        if (proveedor.getIndividuo() != null &&
                proveedor.getIndividuo().getId_individuo() != null) {
            var ind = individuoDao.findById(proveedor.getIndividuo().getId_individuo())
                    .orElseThrow(() -> new IllegalArgumentException("Individuo no existe"));
            proveedor.setIndividuo(ind);
        }

        var ic = proveedor.getInformacionComercial();

        // Si es edición: congelar NIT, Banco y Cuenta
        if (proveedor.getIdProveedor() != null) {
            var actual = proveedorDao.findById(proveedor.getIdProveedor())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no existe"));

            var icActual = actual.getInformacionComercial();
            if (icActual != null) {
                // Opción A: Rechazar si intentan cambiarlos
                if (ic != null) {
                    if (!equalsStr(ic.getNitRut(), icActual.getNitRut())
                            || !equalsStr(ic.getBanco(), icActual.getBanco())
                            || !equalsStr(ic.getNumCuenta(), icActual.getNumCuenta())) {
                        throw new IllegalArgumentException("NIT y cuenta bancaria no pueden modificarse");
                    }
                }

                // Opción B (alternativa): Ignorar cambios y forzar los valores originales
                // if (ic != null) {
                //     ic.setNitRut(icActual.getNitRut());
                //     ic.setBanco(icActual.getBanco());
                //     ic.setNumCuenta(icActual.getNumCuenta());
                // }
            }
        } else {
            // Alta nueva: validar duplicado NIT
            if (ic != null && notBlank(ic.getNitRut())) {
                boolean dup = proveedorDao.existsByInformacionComercial_NitRut(ic.getNitRut());
                if (dup) throw new IllegalArgumentException("Ya existe un proveedor con ese NIT/RUT");
            }
        }

        return proveedorDao.save(proveedor);
    }

    private boolean equalsStr(String a, String b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }
    private boolean notBlank(String s) { return s != null && !s.isBlank(); }

    @Override
    @Transactional
    public void eliminar(Long id) {
        proveedorDao.deleteById(id);
    }
}
