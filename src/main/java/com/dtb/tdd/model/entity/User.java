package com.dtb.tdd.model.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class User{
	private Long id;
	private String nome;
	private String email;
	private String username;
	private List<Permission> permissions;
}
