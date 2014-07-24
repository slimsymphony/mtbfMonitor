package com.nokia.testingservice.austere.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.nokia.testingservice.austere.model.User;
import com.nokia.testingservice.austere.model.User.Role;
import com.nokia.testingservice.austere.service.AuthService;
import com.nokia.testingservice.austere.service.AuthServiceFactory;
import com.nokia.testingservice.austere.util.LogUtils;

public class AuthCheckController extends BaseController {

	private static final long serialVersionUID = -2248299612591817158L;

	@Override
	public void execute( HttpServletRequest request, HttpServletResponse response, Map<String, Object> sessionMap ) throws IOException, ServletException {
		Logger log = LogUtils.getWebLog();
		User userInfo = ( User ) sessionMap.get( "userInfo" );
		if ( userInfo != null )
			log.info( "Found exist session:" + userInfo );
		else {
			log.info( "Not Found  any exist session" );
			response.getWriter().write( "User Not Logon!" );
			return;
		}
		String needRole = request.getParameter( "role" );
		AuthService as = AuthServiceFactory.getInstance();
		if ( userInfo != null && as.getUserInfo( userInfo.getUserID() ).equals( userInfo ) ) {
			try {
				userInfo.setRole( as.getUserInfo( userInfo.getUserID() ).getRole() );
				if ( !userInfo.getRole().equals( Role.Admin ) && needRole != null && !needRole.trim().equals( "" ) ) {
					if ( userInfo.getRole().name().equalsIgnoreCase( needRole ) )
						response.getWriter().write( "true" );
					else {
						response.getWriter().write( "Current operation need "+needRole+", but your role is " + userInfo.getRole().name()+", Please request new Role and try again!" );
						LogUtils.getWebLog().info( "Request Role:"+needRole+", but current user["+userInfo+"] not qualified." );
					}
				}else {
					response.getWriter().write( "true" );
				}
			} catch ( IOException e ) {
				response.sendRedirect( "/error.jsp?message=" + e.getMessage() );
			}
			return;
		} else {
			response.getWriter().write( "Auth User failed,Please contact administrator for help." );
		}
	}

}
