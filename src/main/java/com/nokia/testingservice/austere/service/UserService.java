package com.nokia.testingservice.austere.service;

import java.util.List;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.User;
import com.nokia.testingservice.austere.model.User.Role;

/**
 * Interface for user related operations.
 * 
 * @author Frank Wang
 * @since Jun 14, 2012
 */
public interface UserService {
	
	List<User> getAllUsers() throws ServiceException;
	
	User getUserByID( String userID ) throws ServiceException;

	void delUser( String userID ) throws ServiceException;

	void updateUserExtInfo( String userID, String mail, String fullName ) throws ServiceException;

	void updateUserRole( String userID, Role role ) throws ServiceException;

	void createUser( User user ) throws ServiceException;

	void createUserByID( String userID, Role role ) throws ServiceException;
	
	void createUserByMail( String mail, Role role ) throws ServiceException;
}
