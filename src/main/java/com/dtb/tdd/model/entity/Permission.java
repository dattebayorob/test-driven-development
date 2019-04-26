package com.dtb.tdd.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Permission {
	private Long id;
	private String nome;
}
