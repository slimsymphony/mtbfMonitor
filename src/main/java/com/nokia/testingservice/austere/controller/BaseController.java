package com.nokia.testingservice.austere.controller;

import static com.nokia.testingservice.austere.util.CommonUtils.parseInt;
import static com.nokia.testingservice.austere.util.CommonUtils.parseLong;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nokia.testingservice.austere.exception.ControllerException;
import com.nokia.testingservice.austere.util.LogUtils;

public abstract class BaseController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		try {
			this.doService( request, response );
		} catch ( ControllerException e ) {
			LogUtils.getWebLog().error( "doGet met error.", e );
		}
	}

	@Override
	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		try {
			this.doService( request, response );
		} catch ( ControllerException e ) {
			LogUtils.getWebLog().error( "doPost met error.", e );
		}
	}

	@Override
	public void service( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		try {
			this.doService( request, response );
		} catch ( ControllerException e ) {
			LogUtils.getWebLog().error( "service met error.", e );
		}
	}

	public abstract void execute( HttpServletRequest request, HttpServletResponse response, Map<String, Object> sessionMap ) throws IOException, ServletException;

	protected int getInt( HttpServletRequest request, String name, int defaultValue ) {
		return parseInt( request.getParameter( name ), defaultValue );
	}

	protected long getLong( HttpServletRequest request, String name, long defaultValue ) {
		return parseLong( request.getParameter( name ), defaultValue );
	}

	@SuppressWarnings( "unchecked" )
	public void doService( HttpServletRequest request, HttpServletResponse response ) throws ControllerException {
		Map<String, Object> sessionMap = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		Enumeration<String> enu = session.getAttributeNames();
		while ( enu.hasMoreElements() ) {
			String attName = ( String ) enu.nextElement();
			sessionMap.put( attName, session.getAttribute( attName ) );
		}
		
		for ( String key : sessionMap.keySet() ) {
			session.removeAttribute( key );
		}
		
		try {
			execute( request, response, sessionMap );
		} catch ( Exception e ) {
			throw new ControllerException( e );
		}

		for ( String key : sessionMap.keySet() ) {
			session.setAttribute( key, sessionMap.get( key ) );
		}
		sessionMap.clear();
		sessionMap = null;
	}
}
