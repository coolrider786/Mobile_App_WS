package com.mayank.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;

import com.mayank.app.ws.io.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {
	
	PasswordResetTokenEntity findByToken(String token);
}
