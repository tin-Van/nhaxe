package com.poly.service;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class WebSecurityConfigurerAdapter {

	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub
		
	}

	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Bean
	public SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception {
	    // cấu hình bảo mật của bạn
	    return http.build();
	}


}
