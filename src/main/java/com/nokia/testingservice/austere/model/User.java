package com.nokia.testingservice.austere.model;

import java.sql.Timestamp;

import com.nokia.testingservice.austere.util.CommonUtils;


public class User extends AustereModel<User> {
	public static enum Role {
		Admin, User, DataMaintainer, Anonymous;

		public static Role getRole( String role ) {
			for( Role r : Role.values() ) {
				if(r.name().equalsIgnoreCase( role ))
					return r;
			}
			return Anonymous;
		}
	}

	private String fullName;
	private String userID;
	private Role role;
	private Timestamp createTime;
	private String mail;

	public String getFullName() {
		return fullName;
	}

	public void setFullName( String fullName ) {
		this.fullName = fullName;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID( String userID ) {
		this.userID = userID;
	}

	public Role getRole() {
		return role;
	}

	public void setRole( Role role ) {
		this.role = role;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime( Timestamp createTime ) {
		this.createTime = createTime;
	}

	
	public String getMail() {
		return mail;
	}

	public void setMail( String mail ) {
		this.mail = mail;
	}

	public static User getAnonymousUser() {
		User user = new User();
		user.setCreateTime( new Timestamp( System.currentTimeMillis() ) );
		user.setUserID( "Anonymous" );
		user.setFullName( "Anonymous" );
		user.setRole( Role.Anonymous );
		user.setMail( "" );
		return user;
	}
	
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
