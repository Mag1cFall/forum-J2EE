package com.forum.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.forum.config.ServiceConfig;
import com.forum.dto.BoardDto;
import com.forum.dto.SingleMessageDto;
import com.forum.dto.UserDto;
import com.forum.model.Board;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 */
public class BoardServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		String ctxPath = req.getContextPath();
		PrintWriter pw = resp.getWriter();
		try {
			if (ctxPath.matches("^/[0-9]{0,18}$")) {
				if (ctxPath.equals("/")) {
					pw.write(__doGetAllBoards());
				} else {
					pw.write(__doGetBoardById(Integer.parseInt(ctxPath.substring(1))));
				}
				resp.setStatus(HttpServletResponse.SC_OK);
			} else {
				throw new IllegalArgumentException("没有这样的服务！");
			}
		} catch (IllegalArgumentException e) {
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
				case "/assignModerator" ->
					__doPostAssignModerator(req);
				case "/create" ->
					__doPostCreateBoard(req);
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

	private String __doGetAllBoards() throws JsonProcessingException {
		List<Board> boards = ServiceConfig.boardService.getAllBoards();
		List<BoardDto> boardDtos = boards.stream().map(x -> new BoardDto(x.getId(), x.getName(), x.getDescription(),
				new UserDto(x.getModerator()), x.getCreatedAt())).toList();
		return ServiceConfig.mapperService.writeValueAsString(boardDtos);
	}

	private String __doGetBoardById(int id) throws JsonProcessingException {
		Optional<Board> board = ServiceConfig.boardService.findById(id);
		if (board.isPresent()) {
			return ServiceConfig.mapperService.writeValueAsString(board.get());
		} else {
			throw new IllegalArgumentException("没有这样的贴子！");
		}
	}

	private void __doPostAssignModerator(HttpServletRequest req) throws JsonProcessingException {
		BoardDto board = ServiceConfig.mapperService.readValue(req.getParameter("board"), BoardDto.class);
		UserDto user = ServiceConfig.mapperService.readValue(req.getParameter("user"), UserDto.class);
		ServiceConfig.boardService.assignModerator(board.getId(), user.getUid());
	}

	private void __doPostCreateBoard(HttpServletRequest req) throws JsonProcessingException {
		SingleMessageDto name = ServiceConfig.mapperService.readValue(req.getParameter("boardName"),
				SingleMessageDto.class),
				desc = ServiceConfig.mapperService.readValue(req.getParameter("description"), SingleMessageDto.class),
				modId = ServiceConfig.mapperService.readValue(req.getParameter("userId"), SingleMessageDto.class);
		ServiceConfig.boardService.createBoard(name.getMessage(), desc.getMessage(),
				Integer.parseInt(modId.getMessage()));
	}
}
