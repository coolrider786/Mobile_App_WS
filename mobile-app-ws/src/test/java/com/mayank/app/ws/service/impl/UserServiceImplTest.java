package com.mayank.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mayank.app.ws.exceptions.UserServiceException;
import com.mayank.app.ws.io.entity.AddressEntity;
import com.mayank.app.ws.io.entity.UserEntity;
import com.mayank.app.ws.io.repositories.UserRepository;
import com.mayank.app.ws.shared.AmazonSES;
import com.mayank.app.ws.shared.Utils;
import com.mayank.app.ws.shared.dto.AddressDto;
import com.mayank.app.ws.shared.dto.UserDto;

class UserServiceImplTest {
	@InjectMocks
	UserServiceImpl userService;

	@Mock
	UserRepository userRepository;

	@Mock
	Utils utils;

	@Mock
	AmazonSES amazonSES;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;

	UserEntity userEntity;
	String userId = "hhsdhghs1";
	String encryptedpassword = "fdfgvbgfvgvbvhg4565442";

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Mayank");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(encryptedpassword);
		userEntity.setEmail("mayankrairai786@gmail.com");
		userEntity.setEmailVerificationToken("hfsfhghjafhagf5456446");
		userEntity.setAddresses(getAddressesEntity());
	}

	@Test
	void testGetUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

		UserDto userDto = userService.getUser("mayankrairai786@gmail.com");

		assertNotNull(userDto);

		assertEquals("Mayank", userDto.getFirstName());
	}

	@Test
	final void testGetUser_UsernameNotFoundException() {
		when(userRepository.findByEmail(anyString())).thenReturn(null);

		assertThrows(UsernameNotFoundException.class, () -> {
			userService.getUser("Mayank");
		});
	}

	@Test
	final void testCreateUser_CreateUSerServiceException() {
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressDto());
		userDto.setFirstName("Mayank");
		userDto.setLastName("Rai");
		userDto.setPassword("sahgdahgjc");
		userDto.setEmail("vomayank@gmail.com");

		assertThrows(UserServiceException.class, () -> {
			userService.createUser(userDto);
		});
	}

	@Test
	final void testCreateUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("hfasaghfhda4585");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedpassword);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDto.class));

		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressDto());
		userDto.setFirstName("Mayank");
		userDto.setLastName("Rai");
		userDto.setPassword("sahgdahgjc");
		userDto.setEmail("vomayank@gmail.com");

		UserDto storedUSerDetails = userService.createUser(userDto);
		assertNotNull(storedUSerDetails);
		assertEquals(userEntity.getFirstName(), storedUSerDetails.getFirstName());
		assertEquals(userEntity.getLastName(), storedUSerDetails.getLastName());
		assertNotNull(storedUSerDetails.getUserId());
		assertEquals(storedUSerDetails.getAddresses().size(), userEntity.getAddresses().size());
		verify(utils, times(storedUSerDetails.getAddresses().size())).generateAddressId(30);
		verify(bCryptPasswordEncoder, times(1)).encode("sahgdahgjc");
		verify(userRepository, times(1)).save(any(UserEntity.class));
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

	private List<AddressEntity> getAddressesEntity() {
		List<AddressDto> addressed = getAddressDto();

		Type listType = new TypeToken<List<AddressEntity>>() {
		}.getType();

		return new ModelMapper().map(addressed, listType);
	}

}
