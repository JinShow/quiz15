package com.example.quiz15.vo;

import com.example.quiz15.constants.ConstantsMessage;

import jakarta.validation.constraints.NotBlank;

public class LoginReq {

	@NotBlank(message = ConstantsMessage.EMAIL_ERROR)
	private String email;

	@NotBlank(message = ConstantsMessage.PASSWORD_ERROR)
	private String password;
	
	

	public LoginReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public LoginReq(@NotBlank(message = "Email Error!!") String email,
			@NotBlank(message = "Password Error!!") String password) {
		super();
		this.email = email;
		this.password = password;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
