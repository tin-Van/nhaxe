package com.poly.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import com.poly.DAO.AccountDAO;
import com.poly.entity.Account;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService implements UserDetailsService  {
	
	@Autowired
	AccountDAO dao;  // DAO để thao tác với database
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account userObj = dao.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));
		
		return User.builder()
			.username(userObj.getUsername())
			.password(userObj.getPassword())  // Mã hóa mật khẩu
			.roles(userObj.getRole())
			.build();
	}

	public void loginFormOAth2(OAuth2AuthenticationToken token) {
		String email = token.getPrincipal().getAttribute("email");
		String name = token.getPrincipal().getAttribute("name"); // Lấy thêm thông tin tên nếu cần
		String password = Long.toHexString(System.currentTimeMillis()); // Mật khẩu giả định
		// Kiểm tra xem tài khoản đã tồn tại hay chưa
		Optional<Account> accountOpt = dao.findByUsername(email);
		
		if (accountOpt.isEmpty()) {
			// Nếu chưa có tài khoản thì tạo tài khoản mới và lưu vào database
			Account newAccount = new Account();
			newAccount.setUsername(email);
			newAccount.setPassword(password);  // Bạn có thể mã hóa mật khẩu nếu cần
			newAccount.setRole("Guest"); // Phân quyền mặc định là Guest
			dao.save(newAccount);  // Lưu tài khoản vào database
		}
		
		// Sau khi lưu hoặc nếu đã tồn tại, xác thực người dùng
		UserDetails user = User.builder()
			.username(email)
			.password(password)  // Mật khẩu giả định (sẽ không dùng trong trường hợp OAuth2)
			.roles("Guest")
			.build();
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
