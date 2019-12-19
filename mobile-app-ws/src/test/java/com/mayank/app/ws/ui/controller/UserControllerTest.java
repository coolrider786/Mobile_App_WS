package com.mayank.app.ws.ui.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.mayank.app.ws.service.impl.UserServiceImpl;
import com.mayank.app.ws.shared.dto.AddressDto;
import com.mayank.app.ws.shared.dto.UserDto;
import com.mayank.app.ws.ui.model.response.UserRest;

class UserControllerTest {
	
	@InjectMocks
	UserController userController;
	
	@Mock
	UserServiceImpl userService;
	
	@Mock
	UserDto userDto;
	
	final String USER_ID = "afjsavjsajhvj6544";

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		userDto  = new UserDto();
		userDto.setFirstName("Mayank");
		userDto.setLastName("Rai");
		userDto.setEmail("maynakrairai786@gmail.com");
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setEmailVerificationToken(null);
		userDto.setUserId(USER_ID);
		userDto.setAddresses(getAddressDto());
		userDto.setEncryptedPassword("jfshjshjsc546");
	}

	@Test
	void testGetUser() {
		when(userService.getUserById( anyString() )).thenReturn(userDto);
		
		UserRest userRest = userController.getUser(USER_ID);
		
		assertNotNull(userRest);
		assertEquals(USER_ID, userRest.getUserId());
		assertEquals(userDto.getFirstName(), userRest.getFirstName());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
	}
	
	private List<AddressDto> getAddressDto() {

		AddressDto addressDto = new AddressDto();
		addressDto.setType("shipping");
		addressDto.setCity("Gorakhpur");
		addressDto.setCountry("India");
		addressDto.setStreetName("Janipur");
		addressDto.setPostalName("Dubalui");

		AddressDto billingAddressDto = new AddressDto();
		billingAddressDto.setType("billing");
		billingAddressDto.setCity("Basti");
		billingAddressDto.setCountry("India");
		billingAddressDto.setStreetName("Janipur");
		billingAddressDto.setPostalName("Dubalui");

		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(addressDto);
		addresses.add(billingAddressDto);

		return addresses;
	}

}
