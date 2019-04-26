package com.dtb.tdd.model;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class NomeAndEmailDto {
	@NotNull(message = "Nome should not be null")
	String nome;
	@NotNull(message = "Email should not be null")
	String email;
}
