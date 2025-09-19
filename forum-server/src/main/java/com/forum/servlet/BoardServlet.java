package com.forum.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.forum.config.ServiceConfig;
import com.forum.dto.BoardDto;
import com.forum.dto.CreateBoardDto;
import com.forum.dto.PostDto;
import com.forum.dto.SingleMessageDto;
import com.forum.dto.UserDto;
import com.forum.model.Board;
import com.forum.util.ServletUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 */
public class BoardServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		if (path == null) {
			path = "";
		}
		PrintWriter pw = resp.getWriter();
		try {
			if (path.matches("^/[0-9]{0,10}$")) {
				if (path.equals("/")) {
					pw.write(__doGetAllBoards());
				} else {
					pw.write(__doGetBoardById(Integer.parseInt(path.substring(1))));
				}
				resp.setStatus(HttpServletResponse.SC_OK);
			} else if (path.matches("^/[0-9]{0,10}/post$")) {
				__doGetAllPosts(Integer.parseInt(path.substring(1, path.indexOf('/', 1))));
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
		String path = req.getPathInfo();
		PrintWriter pw = resp.getWriter();
		if (path == null) {
			path = "";
		}
		try {
			if (path.matches("^/[0-9]{0,10}/[0-9a-zA-Z]+$")) {
				int boardId = Integer.parseInt(path.substring(1, path.indexOf('/', 1)));
				switch (path.substring(path.indexOf('/', 1))) {
					case "/assignModerator" ->
						__doPostAssignModerator(req, boardId);
					default ->
						throw new IllegalArgumentException("没有这样的服务！");
				}
			} else if (path.equals("/create")) {
				__doPostCreateBoard(req);
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

	/**
	 * /api/board/[bid]/post
	 */
	private String __doGetAllPosts(int boardId) throws IOException {
		List<PostDto> list = ServiceConfig.postService.getPostsByBoard(boardId).stream()
				.map(x -> new PostDto(x))
				.toList();
		return ServiceConfig.mapperService.writeValueAsString(list);
	}

	/**
	 * /api/board/
	 */
	private String __doGetAllBoards() throws JsonProcessingException {
		List<Board> boards = ServiceConfig.boardService.getAllBoards();
		List<BoardDto> boardDtos = boards.stream().map(x -> new BoardDto(x.getId(), x.getName(), x.getDescription(),
				new UserDto(x.getModerator()), x.getCreatedAt())).toList();
		return ServiceConfig.mapperService.writeValueAsString(boardDtos);
	}

	/**
	 * /api/board/[bid]
	 */
	private String __doGetBoardById(int id) throws JsonProcessingException {
		Optional<Board> board = ServiceConfig.boardService.findById(id);
		if (board.isPresent()) {
			return ServiceConfig.mapperService.writeValueAsString(board.get());
		} else {
			throw new IllegalArgumentException("没有这样的贴子！");
		}
	}

	/**
	 * /api/[bid]/assignModerator
	 */
	private void __doPostAssignModerator(HttpServletRequest req, int boardId) throws IOException {
		UserDto user = ServiceConfig.mapperService.readValue(ServletUtil.getJsonString(req), UserDto.class);
		ServiceConfig.boardService.assignModerator(boardId, user.getUid());
	}

	/**
	 * /api/board/create
	 */
	private void __doPostCreateBoard(HttpServletRequest req) throws IOException {
		CreateBoardDto cbDto = ServiceConfig.mapperService.readValue(ServletUtil.getJsonString(req),
				CreateBoardDto.class);
		ServiceConfig.boardService.createBoard(cbDto.getName(), cbDto.getDesc(),
				cbDto.getModId());
	}
}
