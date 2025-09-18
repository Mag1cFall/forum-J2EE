package com.forum.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.forum.config.ServiceConfig;
import com.forum.dto.BoardDto;
import com.forum.dto.CommentDto;
import com.forum.dto.PostDto;
import com.forum.dto.SingleMessageDto;
import com.forum.dto.UserDto;
import com.forum.model.Post;

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
		String ctxPath = req.getContextPath();
		PrintWriter pw = resp.getWriter();
		try {
			if (ctxPath.matches("^/[0-9]*$")) {
				if (ctxPath.equals("/")) {
					pw.write(__doGetAllPosts(req));
				} else {
					pw.write(__doGetPostById(Integer.parseInt(ctxPath.substring(1))));
				}
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
		String ctxPath = req.getContextPath();
		PrintWriter pw = resp.getWriter();
		try {
			switch (ctxPath) {
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

	private String __doGetAllPosts(HttpServletRequest req) throws JsonProcessingException {
		BoardDto board = ServiceConfig.mapperService.readValue(req.getParameter("board"), BoardDto.class);
		List<PostDto> list = ServiceConfig.postService.getPostsByBoard(board.getId()).stream()
				.map(x -> new PostDto(x.getId(), x.getTitle(), x.getContent(), new UserDto(x.getAuthor()),
						new BoardDto(x.getBoard()), x.getCreatedAt(), x.isPinned(), x.getLikes(), x.getDislikes(),
						x.getComments().stream().map(y -> new CommentDto(y)).toList()))
				.toList();
		return ServiceConfig.mapperService.writeValueAsString(list);
	}

	private String __doGetPostById(int pid) throws JsonProcessingException {
		Optional<Post> postOpt = ServiceConfig.postService.getPostDetails(pid); // 获取帖子详情
		if (postOpt.isPresent()) { // 如果帖子不存在
			return ServiceConfig.mapperService.writeValueAsString(postOpt.get());
		} else {
			throw new IllegalArgumentException("没有这样的贴子！");
		}
	}

	private void __doPostCreatePost(HttpServletRequest req) throws JsonProcessingException {
		SingleMessageDto title = ServiceConfig.mapperService.readValue(req.getParameter("title"),
				SingleMessageDto.class),
				content = ServiceConfig.mapperService.readValue(req.getParameter("content"), SingleMessageDto.class),
				boardId = ServiceConfig.mapperService.readValue(req.getParameter("boardId"), SingleMessageDto.class);
		int userId = ((UserDto) req.getSession().getAttribute("user")).getUid();
		ServiceConfig.postService.createPost(title.getMessage(), content.getMessage(), userId,
				Integer.parseInt(boardId.getMessage()));
	}
}