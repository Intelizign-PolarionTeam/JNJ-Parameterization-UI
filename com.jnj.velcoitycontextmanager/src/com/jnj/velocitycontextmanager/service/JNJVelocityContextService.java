package com.jnj.velocitycontextmanager.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface JNJVelocityContextService {
	
	public void getLiveDocAttachedWithJsonFile (HttpServletRequest req, HttpServletResponse resp) throws Exception;
	
	public void updateLiveDocAttachment (HttpServletRequest req, HttpServletResponse resp) throws Exception;
	
	
}
