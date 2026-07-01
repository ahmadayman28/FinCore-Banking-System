package com.fincore.fincorebank.role.service;

import java.util.List;

import com.fincore.fincorebank.response.Response;
import com.fincore.fincorebank.role.entity.Role;

public interface RoleService {
	Response<Role> createRole(Role roleRequest);
	Response<Role> updateRole(Role roleRequest);
	Response<List<Role>> getAllRoles();
	Response<?> deleteRole(Long id);
}
