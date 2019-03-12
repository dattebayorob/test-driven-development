package com.dtb.restapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dtb.restapi.model.entities.JustAEntity;

public interface JustAEntityService {
	
	Optional<Page<JustAEntity>> findAll(Pageable pageable);
	
	Optional<JustAEntity> findById(Long id);
	
	Optional<List<JustAEntity>> findByName(String name);
		
	JustAEntity save(JustAEntity entity);
	
	void deleteById(Long id);
}
