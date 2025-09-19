package com.forum.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.forum.config.ServiceConfig;
import com.forum.dto.CreatePostDto;
import com.forum.dto.PostDto;
import com.forum.dto.SingleMessageDto;
import com.forum.dto.UserDto;
import com.forum.model.Post;
import com.forum.util.ServletUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PostServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		String path = req.getPathInfo();
		PrintWriter pw = resp.getWriter();
		try {
			if (path.matches("^/[0-9]{0,10}$")) {
				pw.write(__doGetPostById(Integer.parseInt(path.substring(1))));
			} else {
				throw new IllegalArgumentException("没有这样的服务！");
			}
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			pw.write(ServiceConfig.mapperService.writeValueAsString(new SingleMessageDto(e.getMessage())));
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		pw.flush();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		String path = req.getPathInfo();
		PrintWriter pw = resp.getWriter();
		try {
			switch (path) {
				case "/create" ->
					__doPostCreatePost(req);
				default ->
					throw new IllegalArgumentException("没有这样的服务！");
			}
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			pw.write(ServiceConfig.mapperService.writeValueAsString(new SingleMessageDto(e.getMessage())));
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		pw.flush();
	}

	/**
	 * /api/post/[pid]
	 */
	private String __doGetPostById(int pid) throws JsonProcessingException {
		Optional<Post> postOpt = ServiceConfig.postService.getPostDetails(pid);
		if (postOpt.isPresent()) {
			return ServiceConfig.mapperService.writeValueAsString(new PostDto(postOpt.get()));
		} else {
			throw new IllegalArgumentException("没有这样的贴子！");
		}
	}

	/**
	 * /api/post/create
	 */
	private void __doPostCreatePost(HttpServletRequest req) throws IOException {
		CreatePostDto cpDto = ServiceConfig.mapperService.readValue(ServletUtil.getJsonString(req),
				CreatePostDto.class);
		ServiceConfig.postService.createPost(cpDto.getTitle(), cpDto.getContent(),
				((UserDto) req.getSession().getAttribute("user")).getUid(),
				cpDto.getBoardId());
	}
}