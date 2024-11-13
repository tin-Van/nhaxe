package com.poly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.poly.entity.Account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class GlobalInterceptor implements HandlerInterceptor {

	@Autowired
	service session;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		request.setAttribute("uri", request.getRequestURI());
		String uri = request.getRequestURI();
		System.out.println(uri);
		String error = "";
		Account user = session.getSession("user"); // lấy từ session
		if (user == null) { // chưa đăng nhập
			error = "Please login!";
		}
		// không đúng vai trò
		else if (!user.getRole().equals("admin") && uri.startsWith("/admin/")) {
			System.out.println("Access denied!");
			error = "Access denied!";
		}

		if (error.length() > 0) { // có lỗi
			session.saveBySession("security-uri", uri);
			response.sendRedirect("/login?error=" + error);
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv)
			throws Exception {
	}
}
