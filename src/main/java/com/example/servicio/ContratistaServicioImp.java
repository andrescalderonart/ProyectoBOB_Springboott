// src/main/java/com/example/servicio/ContratistaServicioImp.java
package com.example.servicio;

import com.example.dao.ContratistaDao;
import com.example.dao.PersonaDao;
import com.example.domain.Contratista;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContratistaServicioImp implements ContratistaServicio {

    private final ContratistaDao contratistaDao;
    private final PersonaDao personaDao;

    public ContratistaServicioImp(ContratistaDao contratistaDao, PersonaDao personaDao) {
        this.contratistaDao = contratistaDao;
        this.personaDao = personaDao;
    }

    @Override
    public List<Contratista> listar() {
        return contratistaDao.findAll();
    }

    @Override
    public Optional<Contratista> buscarPorId(Long id) {
        return contratistaDao.findById(id);
    }

    @Override
    @Transactional
    public Contratista guardar(Contratista form) {
        // Resolver Persona si viene con id
        if (form.getIdPersona() != null && form.getIdPersona().getIdPersona() != null) {
            form.setIdPersona(
                    personaDao.findById(form.getIdPersona().getIdPersona())
                            .orElseThrow(() -> new IllegalArgumentException("Persona no existe"))
            );
        } else {
            form.setIdPersona(null);
        }

        // === CREAR ===
        if (form.getIdContratista() == null) {
            var ic = form.getInformacionComercial();
            if (ic != null && notBlank(ic.getNitRut())) {
                if (contratistaDao.existsByInformacionComercial_NitRut(ic.getNitRut())) {
                    throw new IllegalArgumentException("Ya existe un contratista con ese NIT/RUT");
                }
            }
            return contratistaDao.save(form);
        }

        // === EDITAR ===
        Contratista entity = contratistaDao.findById(form.getIdContratista())
                .orElseThrow(() -> new IllegalArgumentException("Contratista no existe"));

        var infoForm = form.getInformacionComercial();
        var infoEntity = entity.getInformacionComercial();
        if (infoEntity == null && infoForm != null) {
            infoEntity = new com.example.domain.InformacionComercial();
            entity.setInformacionComercial(infoEntity);
        }

        if (infoForm != null) {
            // “congelar” NIT/Banco/Cuenta (igual que en ProveedorServicioImp)
            if (notBlank(infoForm.getNitRut()) && infoEntity.getNitRut() != null &&
                    !equalsStr(infoForm.getNitRut(), infoEntity.getNitRut())) {
                throw new IllegalArgumentException("NIT/RUT no puede modificarse");
            }
            if (notBlank(infoForm.getBanco()) && infoEntity.getBanco() != null &&
                    !equalsStr(infoForm.getBanco(), infoEntity.getBanco())) {
                throw new IllegalArgumentException("Banco no puede modificarse");
            }
            if (notBlank(infoForm.getNumCuenta()) && infoEntity.getNumCuenta() != null &&
                    !equalsStr(infoForm.getNumCuenta(), infoEntity.getNumCuenta())) {
                throw new IllegalArgumentException("Cuenta no puede modificarse");
            }

            if (infoEntity.getNitRut() == null) infoEntity.setNitRut(infoForm.getNitRut());
            if (infoEntity.getBanco() == null) infoEntity.setBanco(infoForm.getBanco());
            if (infoEntity.getNumCuenta() == null) infoEntity.setNumCuenta(infoForm.getNumCuenta());

            infoEntity.setCorreoElectronico(infoForm.getCorreoElectronico());
            infoEntity.setDireccion(infoForm.getDireccion());
            infoEntity.setFormaPago(infoForm.getFormaPago());
            infoEntity.setProducto(infoForm.getProducto());
        }

        return contratistaDao.save(entity);
    }

    private boolean equalsStr(String a, String b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }
    private boolean notBlank(String s) { return s != null && !s.isBlank(); }

    @Override
    @Transactional
    public void eliminar(Long id) {
        contratistaDao.deleteById(id);
    }
}
