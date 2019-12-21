package com.mayank.app.ws.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {
	
	@Autowired
	Utils utils;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateUserId() {
		String userId = utils.generateUserId(30);
		String userId1 = utils.generateUserId(30);
		assertNotNull(userId);
		assertNotNull(userId1);
		assertTrue(userId.length() == 30);
		assertTrue(!userId.equalsIgnoreCase(userId1));
	}
	
	@Test
	void testHasTokenNotExpired() {
		String token = utils.generateEmailVerificationToken("hsaghaf1312");
		assertNotNull(token);
		
		boolean hasTokenExpired = Utils.hasTokenExpired(token);
		assertFalse(hasTokenExpired);
	}
	
	@Test
	void testHasTokenExpired() {
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJoc2FnaGFmMTMxMiIsImV4cCI6MTU3NzY0NDYzOX0.VEV3hGY6FYxlsYnkCk-FBJKm5m7nolXHnWCbrqSrHisdVrGrHNMd3xX5QLvoBuHQ0ay7GLrOZrYxqFaWbSPpAA";
		boolean hasTokenExpired = Utils.hasTokenExpired(token);
		assertTrue(hasTokenExpired);
	}

}
