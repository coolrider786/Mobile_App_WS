package com.mayank.app.ws.security;

import com.mayank.app.ws.SpringApplicationContext;

public class SecurityConstants {
	
	public static final long 	EXPRIATION_TIME = 864000000; //10 day
	public static final String  TOKEN_PREFIX    = "Bearer ";
	public static final String  HEADER_STRING   = "Authorization";
	public static final String  SIGN_UP_URL     = "/users";
//	public static final String  TOKEN_SECRET    = "jf9i4jgu83nf10";
	
	public static String getTokenSecret() {
		AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
		return appProperties.getTokenSecreat();	
		}
}
