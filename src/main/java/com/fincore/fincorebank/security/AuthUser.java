package com.fincore.fincorebank.security;

import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fincore.fincorebank.auth_users.entity.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthUser implements UserDetails{
	
	private User user;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    return user.getRoles().stream()
	            .map(role -> (GrantedAuthority) () -> role.getName())
	            .toList();
	}

	@Override
	public @Nullable String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

}
