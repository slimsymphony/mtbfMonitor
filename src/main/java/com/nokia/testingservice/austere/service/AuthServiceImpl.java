package com.nokia.testingservice.austere.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;

import com.nokia.testingservice.austere.exception.AuthException;
import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.User;
import com.nokia.testingservice.austere.util.LDAPAuthenticator;
import com.nokia.testingservice.austere.util.LogUtils;

public class AuthServiceImpl implements AuthService {

	private static final Logger log = LogUtils.getServiceLog();
	private static final Hashtable<String, User> cache = new Hashtable<String, User>();
	private static final Hashtable<Long, String> timeslots = new Hashtable<Long, String>();

	private static Thread checker;
	private static final long TIMEOUT = 1000 * 60 * 60; /* 10 minutes */
	static {
		checker = new Thread() {
			public void run() {
				while ( true ) {
					long current = System.currentTimeMillis();
					List<Long> rl = new ArrayList<Long>();
					for ( long key : timeslots.keySet() ) {
						if ( ( current - key ) > TIMEOUT ) {
							rl.add( key );
							String user = timeslots.remove( key );
							cache.remove( user );
							log.info( "Checker remove {} for timeout", user );
						}
					}
					for ( long k : rl ) {
						timeslots.remove( k );
					}
					rl.clear();
					try {
						Thread.sleep( 1000 * 60 );
					} catch ( InterruptedException e ) {
						log.error( "Cache checker thread wait met excxeption", e );
					}
				}
			}
		};
		checker.start();
	}

	@Override
	public void updateCacheUser( User user ) {
		cache.remove( user.getUserID() );
		cache.put( user.getUserID(), user );
	}
	
	@Override
	public boolean authUser( String userID, String password ) throws AuthException {
		LDAPAuthenticator ldap = new LDAPAuthenticator();
		User user = null;
		boolean isAuth = ldap.authenticateUser( userID, password );
		log.info( "User[{}] is authed as {}", userID, isAuth );
		if ( isAuth ) {
			UserService us = UserServiceFactory.getInstance();
			try {
				user = us.getUserByID( userID );
				if ( user != null ) {
					if( (user.getMail()==null||user.getMail().trim()=="") || (user.getFullName()==null||user.getFullName().trim()=="") ) {
						us.updateUserExtInfo( userID, ldap.getMail(), ldap.getFullName() );
					}
				}else {// can't login!
					throw new ServiceException("the User["+userID+"] doesn't have right to manage Austere Management System, please contact Admin to request your access right.");
				}
			} catch ( ServiceException e ) {
				throw new AuthException( e );
			}
			

			if ( cache.get( userID ) != null ) {
				long uni = -1;
				for ( long key : timeslots.keySet() ) {
					if ( timeslots.get( key ).equals( userID ) ) {
						uni = key;
						timeslots.put( System.currentTimeMillis(), userID );
						break;
					}
				}
				if ( uni != -1 )
					timeslots.remove( uni );
			} else {
				cache.put( userID, user );
				timeslots.put( System.currentTimeMillis(), userID );
			}

		}
		return isAuth;
	}

	@Override
	public User getUserInfo( String user ) {
		return cache.get( user );
	}

	@Override
	public void logOff( String user ) throws AuthException {
		cache.remove( user );
		long key = -1L;
		for ( Entry<Long, String> entry : timeslots.entrySet() ) {
			if ( entry.getValue().equals( user ) ) {
				key = entry.getKey();
				break;
			}
		}
		if ( key != -1L )
			timeslots.remove( key );
	}

}
