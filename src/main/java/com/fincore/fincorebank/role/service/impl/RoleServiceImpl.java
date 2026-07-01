package com.fincore.fincorebank.role.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fincore.fincorebank.exceptions.BadRequestException;
import com.fincore.fincorebank.exceptions.NotFoundException;
import com.fincore.fincorebank.response.Response;
import com.fincore.fincorebank.role.entity.Role;
import com.fincore.fincorebank.role.repo.RoleRepo;
import com.fincore.fincorebank.role.service.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class RoleServiceImpl implements RoleService{

	private final RoleRepo roleRepo;
	
	@Override
	public Response<Role> createRole(Role roleRequest) {
		if (roleRepo.findByName(roleRequest.getName()).isPresent()) {
			throw new BadRequestException("Role Already exists");
		}
		Role savedRole = roleRepo.save(roleRequest);
		return Response.<Role>builder()
				.statusCode(HttpStatus.OK.value())
				.message("Role saved successfully")
				.data(savedRole)
				.build();
	}

	@Override
	public Response<Role> updateRole(Role roleRequest) {
		Role role = roleRepo.findById(roleRequest.getId()).orElseThrow(()-> new NotFoundException("Role not found"));
		role.setName(roleRequest.getName());
		Role updatedRole = roleRepo.save(role);
		return Response.<Role>builder()
				.statusCode(HttpStatus.OK.value())
				.message("Role updated successfully")
				.data(updatedRole)
				.build();
	}

	@Override
	public Response<List<Role>> getAllRoles() {
		List<Role> roles = roleRepo.findAll();
		return Response.<List<Role>>builder()
				.statusCode(HttpStatus.OK.value())
				.message("get all roles")
				.data(roles)
				.build();
	}

	@Override
	public Response<?> deleteRole(Long id) {
		if (!roleRepo.existsById(id)) {
			throw new NotFoundException("Role Not Found");
		}
		roleRepo.deleteById(id);
		return Response.builder()
				.statusCode(HttpStatus.OK.value())
				.message("Role Deleted Successfully")
				.build();
	}

}
