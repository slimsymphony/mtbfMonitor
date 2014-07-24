package com.nokia.testingservice.austere.model;

import java.sql.Timestamp;

import com.nokia.testingservice.austere.util.CommonUtils;

/**
 * Define operations of users.
 * 
 * @author Frank Wang
 * @since Jun 14, 2012
 */
public class OperationRecord extends AustereModel<OperationRecord> {
	public static enum OperationType {
		Login,Logout,
		CreateTask,UpdateTask,DeleteTask, 
		AddUser,DeleteUser,UpdateUser,
		AddStation,DeleteStation,UpdateStation,
		AddProduct,UpdateProduct,DeleteProduct,
		AddSite,UpdateSite,DeleteSite,
		AddDatabase,UpdateDatabase,DeleteDatabase,
		AddInstance,UpdateInstance,DeleteInstance,
		Other;
		
		public static OperationType parse( String str ) {
			for ( OperationType type : OperationType.values() ) {
				if ( type.name().equalsIgnoreCase( str ) )
					return type;
			}
			return Other;
		}
	}

	private OperationType type;
	private String userID;
	private Timestamp operationTime;
	private String details;

	public OperationType getType() {
		return type;
	}

	public void setType( OperationType type ) {
		this.type = type;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID( String userID ) {
		this.userID = userID;
	}

	public Timestamp getOperationTime() {
		return operationTime;
	}

	public void setOperationTime( Timestamp operationTime ) {
		this.operationTime = operationTime;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails( String details ) {
		this.details = details;
	}
	
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
