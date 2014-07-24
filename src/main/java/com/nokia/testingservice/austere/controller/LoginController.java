package com.nokia.testingservice.austere.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nokia.testingservice.austere.exception.AuthException;
import com.nokia.testingservice.austere.model.OperationRecord.OperationType;
import com.nokia.testingservice.austere.service.AuthService;
import com.nokia.testingservice.austere.service.AuthServiceFactory;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.LogUtils;
import com.nokia.testingservice.austere.util.OperationRecordUtils;

public class LoginController extends BaseController {
	private static final long serialVersionUID = 1L;

	@Override
	public void execute( HttpServletRequest request, HttpServletResponse response, Map<String, Object> sessionMap ) throws IOException, ServletException {
		String username = request.getParameter("user");
		String password = request.getParameter("pass");
		AuthService as = AuthServiceFactory.getInstance();
		try {
			if ( as.authUser( username, password ) ){
				sessionMap.put( "userInfo", as.getUserInfo( username ));
				LogUtils.getWebLog().info( "User["+username+"] logon." );
				OperationRecordUtils.record( username, OperationType.Login, "User["+username+"] Login into System." );
				response.getWriter().write( "true" );
			}else{
				response.getWriter().write( "Fail to authenticate user:"+ username );
			}
		} catch ( AuthException e ) {
			response.getWriter().write( "Fail to authenticate user:"+ username+","+e.getMessage() );
			LogUtils.getWebLog().error(  "Fail to authenticate user:"+ username +"," + CommonUtils.getErrorStack( e ));
		}
	}

}
