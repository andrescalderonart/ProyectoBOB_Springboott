package com.example.dao;

import com.example.domain.InformacionComercial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InformacionComercialDao extends JpaRepository<InformacionComercial, Long> {
}
