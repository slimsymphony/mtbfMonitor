package com.nokia.testingservice.austere.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nokia.testingservice.austere.exception.AuthException;
import com.nokia.testingservice.austere.model.OperationRecord.OperationType;
import com.nokia.testingservice.austere.model.User;
import com.nokia.testingservice.austere.service.AuthService;
import com.nokia.testingservice.austere.service.AuthServiceFactory;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.LogUtils;
import com.nokia.testingservice.austere.util.OperationRecordUtils;

public class LogoutController extends BaseController {
	private static final long serialVersionUID = 1L;

	@Override
	public void execute( HttpServletRequest request, HttpServletResponse response, Map<String, Object> sessionMap ) throws IOException, ServletException {
		AuthService as = AuthServiceFactory.getInstance();
		User userInfo =  (User) sessionMap.get( "userInfo" );
		if( userInfo == null ) {
			LogUtils.getWebLog().error( "Not a logon user request!" );
			response.getWriter().write( "Not a logon user request!" );
		}else {
			sessionMap.remove( "userInfo" );
			try {
				as.logOff( userInfo.getUserID() );
				LogUtils.getWebLog().info( "User["+userInfo.getUserID()+"] logout." );
				OperationRecordUtils.record( userInfo.getUserID(), OperationType.Logout, "User["+userInfo.getUserID()+"] Logout from System." );
				response.getWriter().write( "true" );
			} catch ( AuthException e ) {
				LogUtils.getWebLog().error( "LogOff user["+userInfo.getUserID()+"] failed.", e );
				response.getWriter().write( "LogOff user["+userInfo.getUserID()+"] failed."+CommonUtils.getErrorStack( e ) );
			}
		}
	}

}	
