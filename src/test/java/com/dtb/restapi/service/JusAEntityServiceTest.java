package com.dtb.restapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import com.dtb.restapi.model.converters.EntityDtoConverter;
import com.dtb.restapi.model.dtos.JustAEntityDto;
import com.dtb.restapi.model.entities.JustAEntity;
import com.dtb.restapi.model.exceptions.ResourceNotFoundException;
import com.dtb.restapi.model.exceptions.ValidationErrors;
import com.dtb.restapi.model.exceptions.messages.ErrorMessages;
import com.dtb.restapi.model.repositories.JustAEntityRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JusAEntityServiceTest {
	private static final String NAME = "name";
	private static final String CPF = "cpf";
	private static final String RG = "rg";
	@MockBean
	JustAEntityRepository repository;
	@MockBean 
	EntityDtoConverter converter;
	@Autowired
	JustAEntityService service;
	@Mock
	JustAEntity entity;
	@Mock
	JustAEntityDto dto;
	Page<JustAEntity> entities = new PageImpl<>(Arrays.asList(JustAEntity.builder().build()));
	
	
	@Test
	public void shouldThrowAExceptionWhenNameisAlreadyinUseOnSave() {
		when(dto.getName()).thenReturn(NAME);
		when(repository.existsByName(NAME)).thenReturn(true);
		when(repository.existsByCpf(Mockito.anyString())).thenReturn(false);
		when(repository.existsByRg(Mockito.anyString())).thenReturn(false);
		try{
			service.save(dto);
			Assert.fail();
		}catch(Exception e) {
			assertThat(e)
				.isInstanceOf(ValidationErrors.class);
		}
		verify(repository, times(1)).existsByName(NAME);
		verify(repository, times(1)).existsByCpf(Mockito.any());
		verify(repository, times(1)).existsByRg(Mockito.any());
	}
	
	@Test
	public void shouldThrowExceptionWhenCpfisAlreadyInUseOnSave() {
		when(dto.getCpf()).thenReturn(CPF);
		when(repository.existsByCpf(CPF)).thenReturn(true);
		try {
			service.save(dto);
			Assert.fail();
		}catch (Exception e) {
			assertThat(e).isInstanceOf(ValidationErrors.class);
		}
		verify(repository, times(1)).existsByName(Mockito.any());
		verify(repository, times(1)).existsByCpf(CPF);
		verify(repository, times(1)).existsByRg(Mockito.any());
	}
	
	@Test
	public void shouldThrowExceptionWhenRgisAlreadyInUseOnSave() {
		when(dto.getRg()).thenReturn(RG);
		when(repository.existsByRg(RG)).thenReturn(true);
		try {
			service.save(dto);
			Assert.fail();
		}catch (Exception e) {
			assertThat(e).isInstanceOf(ValidationErrors.class);
		}
		verify(repository, times(1)).existsByName(Mockito.any());
		verify(repository, times(1)).existsByCpf(Mockito.any());
		verify(repository, times(1)).existsByRg(RG);
	}
	
	@Test
	public void shouldSaveAEntityAndConvertItToDto() {
		when(converter.toEntity(dto)).thenReturn(entity);
		when(repository.save(entity)).thenReturn(entity);
		when(converter.toDto(entity)).thenReturn(dto);
		JustAEntityDto result = service.save(dto);
		assertNotNull(result);
		verify(repository, times(1)).save(entity);
		verify(repository, times(1)).existsByName(Mockito.any());
		verify(repository, times(1)).existsByCpf(Mockito.any());
		verify(repository, times(1)).existsByRg(Mockito.any());
	}
	
	@Test
	public void shouldThrowAExceptionWhenNotFoundById() {
		when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		try {
			service.findById(1L);
			Assert.fail();
		}catch(Exception e) {
			assertThat(e).isInstanceOf(ResourceNotFoundException.class).hasMessage(ErrorMessages.ENTITY_NOT_FOUND);
		}
		verify(repository, times(1)).findById(Mockito.anyLong());
	}
	
	@Test
	public void shouldThrowAExceptionWhenEntityIsntEnabledOnFindById() {
		when(entity.isEnabled()).thenReturn(false);
		when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(entity));
		when(converter.toDto(entity)).thenReturn(dto);		
		try {
			service.findById(1L);
			Assert.fail();
		}catch(Exception e) {
			assertThat(e).isInstanceOf(ResourceNotFoundException.class).hasMessage(ErrorMessages.ENTITY_NOT_FOUND);
		}
		verify(repository, times(1)).findById(Mockito.anyLong());
		verify(converter, times(0)).toDto(entity);
	}
	
	@Test
	public void shouldReturnAEntityForGivenId() {
		when(entity.isEnabled()).thenReturn(true);
		when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(entity));
		when(converter.toDto(entity)).thenReturn(dto);
		JustAEntityDto result = service.findById(1L);
		assertNotNull(result);
		assertEquals(result, dto);
		verify(repository, times(1)).findById(Mockito.anyLong());
		verify(converter, times(1)).toDto(entity);
	}
	
	@Test
	public void shouldReturnEntitiesPaginated() {
		when(repository.findByEnabled(Mockito.anyBoolean(), Mockito.any())).thenReturn(entities);
		when(converter.toDto(Mockito.any())).thenReturn(dto);
		Page<JustAEntityDto> result = service.findAll(PageRequest.of(0, 10));
		assertThat(result).contains(dto);
		verify(converter).toDto(Mockito.any());
		verify(repository, times(1)).findByEnabled(Mockito.anyBoolean(), Mockito.any());
	}
	
	@Test
	public void shouldThrowAExceptionWhenUpdatingNameWithInUseName() {
		Long id = 1L;
		when(dto.getId()).thenReturn(id);
		when(dto.getName()).thenReturn(NAME);
		when(entity.getName()).thenReturn("OldName");
		when(entity.getCpf()).thenReturn(CPF);
		when(dto.getCpf()).thenReturn(CPF);
		when(entity.getRg()).thenReturn("oldRg");
		when(dto.getRg()).thenReturn(RG);
		when(entity.isEnabled()).thenReturn(true);
		when(repository.findById(id)).thenReturn(Optional.ofNullable(entity));
		when(repository.existsByName(NAME)).thenReturn(true);
		when(repository.existsByCpf(Mockito.any())).thenReturn(false);
		when(repository.existsByRg(Mockito.any())).thenReturn(false);
		try {
			service.update(dto);
			Assert.fail();
		}catch (Exception e) {
			assertThat(e).isInstanceOf(ValidationErrors.class);
		}
		verify(dto, times(2)).getName();
		verify(repository, times(1)).findById(id);
		verify(repository, times(0)).existsByCpf(Mockito.any());
		verify(repository, times(1)).existsByRg(Mockito.any());
	}
	@Test
	public void shouldThrowAExceptionWhenUpdatingCpfWithInUseCpf() {
		Long id = 1L;
		when(dto.getId()).thenReturn(id);
		when(dto.getName()).thenReturn(NAME);
		when(entity.getName()).thenReturn("OldName");
		when(entity.getCpf()).thenReturn("OldCpf");
		when(dto.getCpf()).thenReturn(CPF);
		when(entity.getRg()).thenReturn(RG);
		when(dto.getRg()).thenReturn(RG);
		when(entity.isEnabled()).thenReturn(true);
		when(repository.findById(id)).thenReturn(Optional.ofNullable(entity));
		when(repository.existsByName(NAME)).thenReturn(false);
		when(repository.existsByCpf(CPF)).thenReturn(true);
		when(repository.existsByRg(Mockito.any())).thenReturn(false);
		try {
			service.update(dto);
			Assert.fail();
		}catch (Exception e) {
			assertThat(e).isInstanceOf(ValidationErrors.class);
		}
		verify(dto, times(2)).getName();
		verify(repository, times(1)).findById(id);
		verify(repository, times(1)).existsByCpf(Mockito.any());
		verify(repository, times(0)).existsByRg(Mockito.any());
	}
	@Test
	public void shouldThrowAExceptionWhenUpdatingRgWithInUseRg() {
		Long id = 1L;
		when(dto.getId()).thenReturn(id);
		when(dto.getName()).thenReturn(NAME);
		when(entity.getName()).thenReturn(NAME);
		when(entity.getCpf()).thenReturn("OldCpf");
		when(dto.getCpf()).thenReturn(CPF);
		when(entity.getRg()).thenReturn("oldRg");
		when(dto.getRg()).thenReturn(RG);
		when(entity.isEnabled()).thenReturn(true);
		when(repository.findById(id)).thenReturn(Optional.ofNullable(entity));
		when(repository.existsByName(NAME)).thenReturn(false);
		when(repository.existsByCpf(Mockito.any())).thenReturn(false);
		when(repository.existsByRg(RG)).thenReturn(true);
		try {
			service.update(dto);
			Assert.fail();
		}catch (Exception e) {
			assertThat(e).isInstanceOf(ValidationErrors.class);
		}
		verify(dto, times(1)).getName();
		verify(repository, times(1)).findById(id);
		verify(repository, times(1)).existsByCpf(Mockito.any());
		verify(repository, times(1)).existsByRg(RG);
	}
	
	@Test
	public void shouldUpdateAndReturnADtoOfAEntityById() {
		Long id = 1L;
		when(dto.getId()).thenReturn(id);
		when(dto.getName()).thenReturn(NAME);
		when(entity.getName()).thenReturn("OldName");
		when(entity.getCpf()).thenReturn("OldCpf");
		when(dto.getCpf()).thenReturn(CPF);
		when(entity.getRg()).thenReturn("oldRg");
		when(dto.getRg()).thenReturn(RG);
		when(entity.isEnabled()).thenReturn(true);
		when(repository.findById(id)).thenReturn(Optional.ofNullable(entity));
		when(repository.existsByName(NAME)).thenReturn(false);
		when(repository.existsByCpf(CPF)).thenReturn(false);
		when(repository.existsByRg(RG)).thenReturn(false);
		when(repository.save(Mockito.any())).thenReturn(entity);
		when(converter.toDto(entity)).thenReturn(dto);

		JustAEntityDto result = service.update(dto);
		
		verify(repository, times(1)).findById(id);
		verify(repository, times(1)).existsByName(NAME);		
		verify(repository, times(1)).existsByCpf(CPF);
		verify(repository, times(1)).existsByRg(RG);
		verify(repository, times(1)).save(entity);
		
	}
	
	@Test
	public void shouldThrowExceptionWhenNotFoundOnUpdate() {
		Long id = 1L;
		when(dto.getId()).thenReturn(id);
		when(repository.findById(id)).thenReturn(Optional.empty());
		try {
			service.update(dto);
			Assert.fail();
		}catch (Exception e) {
			assertThat(e).isInstanceOf(ResourceNotFoundException.class);
		}
		verify(repository).findById(id);
	}
	
}
