package com.dtb.tdd.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.dtb.tdd.model.NomeAndEmailDto;
import com.dtb.tdd.model.entity.User;
import com.dtb.tdd.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService service;

	@GetMapping
	public ResponseEntity<List<User>> testController() {
		
		List<User> users = service.find().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Content for this filter"));
		return ResponseEntity.ok(users);
	}
	@GetMapping("/search")
	public ResponseEntity<User> findByNameAndEmail(@Validated NomeAndEmailDto dto){
		
		User user = service.findByNomeAndEmail(dto.getNome(), dto.getEmail()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));
		return ResponseEntity.ok(user);
	}
	@GetMapping("/search2")
	public ResponseEntity<User> findByNameAndEmail2(
			@RequestParam("nome") String nome,
			@RequestParam("email") String email){
		User user = service.findByNomeAndEmail(nome, email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));
		return ResponseEntity.ok(user);
	}
}
