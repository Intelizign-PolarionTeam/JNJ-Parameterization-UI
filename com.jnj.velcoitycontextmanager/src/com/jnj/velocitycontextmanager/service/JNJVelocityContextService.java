package com.jnj.velocitycontextmanager.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface JNJVelocityContextService {
	
	public void getPolarionJSONFiles(HttpServletRequest req, HttpServletResponse resp) throws Exception;
	
	public void uploadJSONFile(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
