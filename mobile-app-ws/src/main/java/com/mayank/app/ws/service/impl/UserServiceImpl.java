package com.mayank.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mayank.app.ws.exceptions.UserServiceException;
import com.mayank.app.ws.io.entity.PasswordResetTokenEntity;
import com.mayank.app.ws.io.entity.UserEntity;
import com.mayank.app.ws.io.repositories.PasswordResetTokenRepository;
import com.mayank.app.ws.io.repositories.UserRepository;
import com.mayank.app.ws.service.UserService;
import com.mayank.app.ws.shared.AmazonSES;
import com.mayank.app.ws.shared.Utils;
import com.mayank.app.ws.shared.dto.AddressDto;
import com.mayank.app.ws.shared.dto.UserDto;
import com.mayank.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	AmazonSES amazonSES;

	@Override
	public UserDto createUser(UserDto user) {

		if (userRepository.findByEmail(user.getEmail()) != null)
			throw new UserServiceException("Record alredy Exist !");
		
		for(int i=0;i<user.getAddresses().size();i++)
		{
			AddressDto address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, address);
		}

//		UserEntity userEntity = new UserEntity();
//		BeanUtils.copyProperties(user, userEntity);
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);
		
		String publicUserId = utils.generateUserId(10);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);
		UserEntity storedUserDetails = userRepository.save(userEntity);
//		UserDto returnValue = new UserDto();
//		BeanUtils.copyProperties(storedUserDetails, returnValue);
		UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

//		Send Email to user to verify their email address
		amazonSES.verifyEmail(returnValue);
		
		return returnValue;
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), userEntity.getEmailVerificationStatus(),
				true, true, true, new ArrayList<>());
		
//		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUserById(String userId) {
		UserDto returnValue = new UserDto();

		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException("User Wit ID : " + userId + "not found");
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto userDto) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		UserEntity updatedDetails = userRepository.save(userEntity);
		BeanUtils.copyProperties(updatedDetails, returnValue);
		return returnValue;
	}

	@Transactional
	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		userRepository.delete(userEntity);
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValue = new ArrayList<>();

		if (page > 0)
			page -= 1;

		Pageable pageableRequest = PageRequest.of(page, limit);
		Page<UserEntity> userPage = userRepository.findAll(pageableRequest);
		List<UserEntity> users = userPage.getContent();

		for (UserEntity userEntity : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValue.add(userDto);
		}

		return returnValue;
	}

	@Override
	public boolean verifyEmailToken(String token) {
		boolean returnValue = false;	
		
//		find the user by token
		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);
		
		if(userEntity != null) {
			boolean hastokenExpired = Utils.hasTokenExpired(token);
			if(!hastokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity);
				returnValue = true;
			}
		}
		return returnValue;
	}

	@Override
	public boolean requestPasswordReset(String email) {
		boolean returnValue = false;
		
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) {
			return returnValue;
		}
		
		String token = utils.generatePasswordResetToken(userEntity.getUserId());
		
		PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(userEntity);
		passwordResetTokenRepository.save(passwordResetTokenEntity);
		
		returnValue = new AmazonSES().sendPasswordResetRequest(
				userEntity.getFirstName(),
				userEntity.getEmail(),
				token);
		
		return returnValue;
	}

	@Override
	public boolean resetPassword(String token, String password) {
		boolean returnValue = false;
		
		if(Utils.hasTokenExpired(token)) {
			return returnValue;
		}
		
		PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);
		
//		prepare new password
		String encodeNewPassword = bCryptPasswordEncoder.encode(password);
		
//		Update User Password
		UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
		userEntity.setEncryptedPassword(encodeNewPassword);
		UserEntity saveUserEntity = userRepository.save(userEntity);
		
//		Verify if password has Successfully saved
		if(saveUserEntity != null && saveUserEntity.getEncryptedPassword().equalsIgnoreCase(encodeNewPassword)) {
			returnValue = true;
			
//		Remove Password Reset Token from database
		passwordResetTokenRepository.delete(passwordResetTokenEntity);
		
		}
		
		return returnValue;
	}

}
