package com.mayank.app.ws.ui.model.response;

public enum ErrorMessages {
	
	MISSING_REQUIRED_FILED("Missing required filed. Please check the documentation for required field"),
	RECORD_ALREDY_EXISTS("Record already exists"),
	INTERNAL_SERVER_ERROR("Internal Server error"),
	NO_RECORD_FOUND("Record with provied id is not found"),
	AUTHENTICATION_FAILD("Authentication Failed"),
	COULD_NOT_UPDATE_RECORD("Could not update record"),
	COULD_NOT_DELETE_RECORD("Could not delete record"),
	EMAIL_ADDRESS_NOT_VERIFIED("Email address could not be verified");
	
	private String errorMessage;
	
	ErrorMessages(String errorMessage){
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		
		this.errorMessage = errorMessage;
	}

}
