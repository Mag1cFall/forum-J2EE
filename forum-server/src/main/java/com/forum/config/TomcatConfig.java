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
		tomcat.setPort(8080);
		tomcat.setBaseDir("target/tomcat/");
		tomcat.getConnector();
		Context ctx = tomcat.addContext("/api", null);
		__registerServlet(tomcat);
		__registerServletMapping(ctx);
		tomcat.init();
		tomcat.start();
		tomcat.getServer().await();
	}

	private static void __registerServlet(Tomcat tomcat)
			throws LifecycleException {
		tomcat.addServlet("/api", "user_servlet", new AuthServlet());
		tomcat.addServlet("/api", "board_servlet", new BoardServlet());
		tomcat.addServlet("/api", "post_servlet", new PostServlet());
	}

	private static void __registerServletMapping(Context ctx) {
		ctx.addServletMappingDecoded("/auth/*", "user_servlet");
		ctx.addServletMappingDecoded("/board/*", "board_servlet");
		ctx.addServletMappingDecoded("/post/*", "post_servlet");
	}
}
