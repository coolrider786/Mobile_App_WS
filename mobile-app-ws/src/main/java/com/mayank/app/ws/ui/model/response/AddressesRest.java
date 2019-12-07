package com.mayank.app.ws.ui.model.response;

public class AddressesRest {

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getPostalName() {
		return postalName;
	}

	public void setPostalName(String postalName) {
		this.postalName = postalName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String addressId;
	private String city;
	private String country;
	private String streetName;
	private String postalName;
	private String type;
}
