package com.mayank.app.ws.ui.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mayank.app.ws.service.AddressService;
import com.mayank.app.ws.service.UserService;
import com.mayank.app.ws.shared.dto.AddressDto;
import com.mayank.app.ws.shared.dto.UserDto;
import com.mayank.app.ws.ui.model.request.UserDetailsRequestModel;
import com.mayank.app.ws.ui.model.response.AddressesRest;
import com.mayank.app.ws.ui.model.response.OperationStatusModel;
import com.mayank.app.ws.ui.model.response.RequestOperationStatus;
import com.mayank.app.ws.ui.model.response.UserRest;


@RestController
@RequestMapping("/users") // http://localhost:8080/users
public class UserController {

	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressesService;
	
	@Autowired
	AddressService addressService;

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable String id) {
		UserRest returnValue = new UserRest();

		UserDto userDto = userService.getUserById(id);

		BeanUtils.copyProperties(userDto, returnValue);

		return returnValue;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

		UserRest returnValue = new UserRest();
		if (userDetails.getFirstName().isEmpty())
			throw new NullPointerException("This is a trial message");
//		UserDto userDto = new UserDto();
//		BeanUtils.copyProperties(userDetails, userDto);

		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userDto);
//		BeanUtils.copyProperties(createdUser, returnValue);
		returnValue = modelMapper.map(createdUser, UserRest.class);

		return returnValue;
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails)
			throws Exception {

		UserRest returnValue = new UserRest();
		if (userDetails.getFirstName().isEmpty())
			throw new NullPointerException("This is a trial message");
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);

		return returnValue;
	}

	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());

		userService.deleteUser(id);

		returnValue.setOpertaionResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}

	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "2") int limit) {
		List<UserRest> returnValue = new ArrayList<>();

		List<UserDto> users = userService.getUsers(page, limit);

		for (UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}
		return returnValue;
	}

	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {
		List<AddressesRest> addressesListRestModel = new ArrayList<>();

		List<AddressDto> addressDto = addressesService.getAddresses(id);
		
		if(addressDto != null && !addressDto.isEmpty()) {
			java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
//			ModelMapper modelMapper = new ModelMapper();
			addressesListRestModel = new ModelMapper().map(addressDto, listType);
			
			for(AddressesRest addressRest : addressesListRestModel) {
				Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(addressRest.getAddressId(), id)).withRel("address");
				addressRest.add(addressLink);
				
				Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
				addressRest.add(userLink);
			}
			
		}

		return new CollectionModel<>(addressesListRestModel);
	}
	
	@GetMapping(path = "/{id}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public EntityModel<AddressesRest> getUserAddress(@PathVariable String addressId, @PathVariable String id) {
		
		AddressDto addressDto = addressService.getAddress(addressId);
		
		
		ModelMapper modelMapper = new ModelMapper();
//		By Using Method On MethodOn 
		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(addressId, id)).withSelfRel();
		Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
//		By Using Method simple link to
		Link addressesLink = linkTo(UserController.class).slash(id).slash("addresses").withRel("addresses");
		AddressesRest addressRest = modelMapper.map(addressDto, AddressesRest.class);
		addressRest.add(addressLink);
		addressRest.add(userLink);
		addressRest.add(addressesLink);
		return new EntityModel<>(addressRest);
		
	}
}
