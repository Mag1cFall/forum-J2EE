package com.forum.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.forum.config.ServiceConfig;
import com.forum.dto.AuthDto;
import com.forum.dto.SingleMessageDto;
import com.forum.dto.UserDto;
import com.forum.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 */
public class AuthServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		String ctxPath = req.getContextPath();
		PrintWriter pw = resp.getWriter();
		try {
			switch (ctxPath) {
				case "/login" ->
					__doPostLogin(req);
				case "/register" ->
					__doPostRegister(req);
				case "/logout" ->
					__doPostLogout(req);
				default ->
					throw new IllegalArgumentException("没有这样的服务！");
			}
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (IllegalArgumentException e) {
			pw.write(ServiceConfig.mapperService.writeValueAsString(new SingleMessageDto(e.getMessage())));
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		pw.flush();
	}

	private void __doPostLogin(HttpServletRequest req)
			throws JsonProcessingException {
		AuthDto authDto = ServiceConfig.mapperService.readValue(req.getParameter("auth"), AuthDto.class);
		Optional<User> userOpt = ServiceConfig.userService.login(authDto.getUsername(), authDto.getPassword());
		if (userOpt.isPresent()) {
			HttpSession session = req.getSession();
			session.setAttribute("user", new UserDto(userOpt.get()));
		} else {
			throw new IllegalArgumentException("用户不存在！");
		}
	}

	private void __doPostRegister(HttpServletRequest req)
			throws JsonProcessingException {
		AuthDto authDto = ServiceConfig.mapperService.readValue(req.getParameter("auth"), AuthDto.class);
		User user = ServiceConfig.userService.register(authDto.getUsername(), authDto.getPassword());
		HttpSession session = req.getSession();
		session.setAttribute("user", new UserDto(user));
	}

	private void __doPostLogout(HttpServletRequest req) {
		HttpSession session = req.getSession();
		session.removeAttribute("user");
	}

}
