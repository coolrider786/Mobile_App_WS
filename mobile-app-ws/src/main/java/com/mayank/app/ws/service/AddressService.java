package com.mayank.app.ws.service;

import java.util.List;

import com.mayank.app.ws.shared.dto.AddressDto;

public interface AddressService {
	List<AddressDto> getAddresses(String id);
	AddressDto getAddress(String addressId);
}
