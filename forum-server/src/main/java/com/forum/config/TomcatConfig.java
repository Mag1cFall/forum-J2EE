package com.forum.config;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import com.forum.servlet.AuthServlet;
import com.forum.servlet.BoardServlet;
import com.forum.servlet.PostServlet;

/**
 */
public class TomcatConfig {

	private TomcatConfig() {
	}

	public static void start()
			throws LifecycleException {
		Tomcat tomcat = new Tomcat();
		tomcat.setHostname("localhost");
		tomcat.setPort(8000);
		tomcat.getConnector();
		Context ctx = tomcat.addContext("", "src/main");
		__registerServlet(tomcat);
		__registerServletMapping(ctx);
	}

	private static void __registerServlet(Tomcat tomcat)
			throws LifecycleException {
		tomcat.addServlet("", "user_servlet", new AuthServlet());
		tomcat.addServlet("", "board_servlet", new BoardServlet());
		tomcat.addServlet("", "post_servlet", new PostServlet());
	}

	private static void __registerServletMapping(Context ctx) {
		ctx.addServletMappingDecoded("/api/auth/*", "user_servlet");
		ctx.addServletMappingDecoded("/api/board/*", "board_servlet");
		ctx.addServletMappingDecoded("/api/post/*", "post_servlet");
	}
}
