package com.dtb.tdd.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.dtb.tdd.model.entity.Permission;
import com.dtb.tdd.model.entity.User;
import com.dtb.tdd.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UserService service;
	private List<User> users;
	
	@Before
	public void init() {
		 users = Arrays.asList(
				User.builder().id(Long.valueOf(1)).nome("User 1").email("user1@email").username("user1")
						.permissions(Arrays.asList(Permission.builder().id(1L).nome("P 1").build(),
								Permission.builder().id(1L).nome("P 2").build()))
						.build(),
				User.builder().id(5L).nome("User 2").email("user2@email").username("user2")
						.permissions(Arrays.asList(Permission.builder().id(1L).nome("P 1").build(),
								Permission.builder().id(1L).nome("P 2").build()))
						.build(),
				User.builder().id(2L).nome("User 3").email("user3@email").username("user3")
						.permissions(Arrays.asList(Permission.builder().id(1L).nome("P 1").build(),
								Permission.builder().id(1L).nome("P 2").build()))
						.build(),
				User.builder().id(3L).nome("User 4").email("user4@email").username("user4")
						.permissions(Arrays.asList(Permission.builder().id(1L).nome("P 1").build(),
								Permission.builder().id(1L).nome("P 2").build()))
						.build(),
				User.builder().id(4L).nome("User 4").email("user4@email").username("user4")
						.permissions(Arrays.asList(Permission.builder().id(1L).nome("P 1").build(),
								Permission.builder().id(1L).nome("P 2").build()))
						.build());
		 BDDMockito.given(service.find()).willReturn(Optional.of(users));
		 BDDMockito.given(service.findById(1L)).willReturn(users.stream().filter(u -> u.getId().equals(1L)).findFirst());
		 BDDMockito.given(service.findById(5L)).willReturn(Optional.empty());
		 BDDMockito.given(service.findByNomeAndEmail("notfound", "notfound")).willReturn(Optional.empty());
		 BDDMockito.given(service.findByNomeAndEmail("found", "found")).willReturn(users.stream().filter(u -> u.getNome().contains("User 1") && u.getEmail().contains("user1@email")).findFirst());
	}

	@Test
	public void shouldReturnArrayOfUsers() throws Exception {
		mockMvc.perform(get("/user")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(users.size())));
	}

	@Test
	public void shouldReturnAUserByNameAndEmail() throws Exception{
		mockMvc.perform(get("/user/search").param("nome", "found").param("email", "found"))
			.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(service.findById(1L).get().getId()));
	}
	@Test
	public void shouldReturnNotFoundWhenNameAndEmailDontMatch() throws Exception{
		mockMvc.perform(get("/user/search").param("nome", "notfound").param("email", "notfound")).andExpect(status().isNotFound())
		.andExpect(jsonPath("$").value(containsString("User Not Found")));
	}
	@Test
	public void shouldReturnBadRequestWhenNoParamsAreSend() throws Exception {
		mockMvc.perform((get("/user/search")))
			.andExpect(status().isBadRequest()).andDo(print());
	}
	@Test
	public void shouldReturnBadRequestWhenNoParams2() throws Exception {
		mockMvc.perform(get("/user/search2"))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("Param needed for query 'nome' is missing"))
			.andDo(print());
	}
}

