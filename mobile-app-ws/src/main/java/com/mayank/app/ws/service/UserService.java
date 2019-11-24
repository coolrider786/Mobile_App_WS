package com.mayank.app.ws.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.mayank.app.ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
	
	UserDto createUser(UserDto user);
	UserDto getUser(String email);
	UserDto getUserById(String userId);
}
