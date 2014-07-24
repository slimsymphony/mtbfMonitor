package com.nokia.testingservice.austere.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.User;
import com.nokia.testingservice.austere.model.User.Role;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.DbUtils;
import com.nokia.testingservice.austere.util.LDAPAuthenticator;
import com.nokia.testingservice.austere.util.LogUtils;

/**
 * Implementation of User service.
 * 
 * @author Frank Wang
 * @since Jun 14, 2012
 */
public class UserServiceImpl implements UserService {

	@Override
	public List<User> getAllUsers() throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<User> users = new ArrayList<User>();
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "select * from Users order by CreateTime" );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				users.add( encap( rs ) );
			}

		} catch ( Exception e ) {
			throw new ServiceException( "Get all Users failed", e );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return users;
	}

	@Override
	public User getUserByID( String userID ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		User user = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "select * from Users where UserID=?" );
			ps.setString( 1, userID );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				user = encap( rs );
			}
			if ( user == null )
				LogUtils.getServiceLog().warn( "Not found a User with userID:" + userID );
		} catch ( Exception e ) {
			throw new ServiceException( "Get User By ID failed, userID=" + userID, e );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return user;
	}

	private User encap( ResultSet rs ) throws SQLException {
		User user = new User();
		user.setUserID( rs.getString( "UserID" ) );
		user.setRole( Role.getRole( rs.getString( "Role" ) ) );
		user.setFullName( rs.getString( "FullName" ) );
		user.setMail( rs.getString( "mail" ) );
		user.setCreateTime( CommonUtils.gmt2local( rs.getTimestamp( "CreateTime" ), TimeZone.getDefault() ) );
		return user;
	}

	@Override
	public void delUser( String userID ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "delete from Users where UserID=?" );
			ps.setString( 1, userID );
			ps.executeUpdate();
		} catch ( Exception e ) {
			throw new ServiceException( "Delete User By ID failed, userID=" + userID, e );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
	}

	@Override
	public void updateUserRole( String userID, Role role ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "update Users set Role=? where UserID=?" );
			ps.setString( 1, role.name() );
			ps.setString( 2, userID );
			ps.executeUpdate();
			User user = this.getUserByID( userID );
			AuthService as = AuthServiceFactory.getInstance();
			as.updateCacheUser( user );
		} catch ( Exception e ) {
			throw new ServiceException( "Update User Role failed, userID=" + userID + ", role=" + role, e );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
	}

	@Override
	public void updateUserExtInfo( String userID, String mail, String fullName ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "update Users set Mail=?, FullName=? where UserID=?" );
			ps.setString( 1, mail );
			ps.setString( 2, fullName );
			ps.setString( 3, userID );
			ps.executeUpdate();
		} catch ( Exception e ) {
			throw new ServiceException( "Update User Role failed, userID=" + userID + ", mail=" + mail + ",fullName=" + fullName, e );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
	}

	@Override
	public void createUserByID( String userID, Role role ) throws ServiceException {
		String mail = LDAPAuthenticator.getUserInfoFromNoe( userID, "mail" );
		String cn = LDAPAuthenticator.getUserInfoFromNoe( userID, "cn" );
		if ( mail == null || cn == null || mail.trim().equals( "" ) || cn.trim().equals( "" ) )
			throw new ServiceException( "User not found.Please check your input noe value." );
		User user = new User();
		user.setFullName( cn );
		user.setMail( mail );
		user.setRole( role );
		user.setUserID( userID );
		createUser( user );
	}
	
	@Override
	public void createUserByMail( String mail, Role role ) throws ServiceException {
		String noe = LDAPAuthenticator.getUserInfo( "mail", mail, "uid" );
		String cn = LDAPAuthenticator.getUserInfo( "mail", mail, "cn" );
		if ( mail == null || cn == null || mail.trim().equals( "" ) || cn.trim().equals( "" ) )
			throw new ServiceException( "User not found.Please check your input noe value." );
		User user = new User();
		user.setFullName( cn );
		user.setMail( mail );
		user.setRole( role );
		user.setUserID( noe );
		createUser( user );
	}

	@Override
	public void createUser( User user ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "insert into Users(UserID,Role,mail,FullName,CreateTime) values(?,?,?,?,?)" );
			ps.setString( 1, user.getUserID() );
			ps.setString( 2, user.getRole().name() );
			ps.setString( 3, user.getMail() );
			ps.setString( 4, user.getFullName() );
			ps.setTimestamp( 5, CommonUtils.local2gmt( new Timestamp(System.currentTimeMillis()), TimeZone.getDefault() ) );
			ps.executeUpdate();
		} catch ( Exception e ) {
			throw new ServiceException( "create new User failed, user=" + user, e );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
	}

}
