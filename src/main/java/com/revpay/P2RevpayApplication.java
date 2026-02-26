package com.revpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class P2RevpayApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(P2RevpayApplication.class, args);
	}

		/*
		 * role created saved
		 * User created
		 * wallet created and add to user field
		 * role adding as field to user
		 * while adding role it also creates user-role and role added to this user-role and this user-role added to user-roles list
		 * now saving user
		 * ABOVE IS OVERALL PROCESS
		 */
}
