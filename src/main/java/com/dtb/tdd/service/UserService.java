package com.dtb.tdd.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dtb.tdd.model.entity.User;

@Service
public class UserService {
	public Optional<List<User>> find() {
		return Optional.of(new ArrayList<>());
	}

	public Optional<User> findById(Long id) {
		return Optional.empty();
	}

	public Optional<User> findByNomeAndEmail(String nome, String email) {
		return Optional.empty();
	}
}
