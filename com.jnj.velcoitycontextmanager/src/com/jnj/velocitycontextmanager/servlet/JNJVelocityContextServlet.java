package com.jnj.velocitycontextmanager.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.jnj.velocitycontextmanager.impl.JNJVelocityContextImpl;
import com.jnj.velocitycontextmanager.service.JNJVelocityContextService;

@MultipartConfig
public class JNJVelocityContextServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private JNJVelocityContextService jnjVelocityContextService;
	
	public void init() throws ServletException {
		super.init();
		this.jnjVelocityContextService = new JNJVelocityContextImpl();	
	}
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		System.out.println("Get action is"+action);
		try {
			if (action != null) {
				switch (action) {
				case "getLiveDocAttachedWithJson":
					jnjVelocityContextService.getLiveDocAttachedWithJsonFile(req, resp);
					break;
				default:
					throw new IllegalArgumentException("Invalid action specified");
				}
			}
		
		if (action == null) {
			getServletContext().getRequestDispatcher("/static/index.html").forward(req, resp);
		}
		} catch (Exception e) {
			System.out.println("Error Message is"+ e.getMessage());
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		System.out.println("upload JSONFile Action"+action);
		try {
		if (action != null) {
			switch (action) {
			case "updateLiveDocAttachment":
				jnjVelocityContextService.updateLiveDocAttachment(req, resp);
				break;
			default:
				throw new IllegalArgumentException("Invalid action specified");
			}
		}
		}catch(Exception e) {
			System.out.println("Error Message is"+e.getMessage());
		}
	}
	
}
