package com.nokia.testingservice.austere.service;

/**
 * Factory for book service.
 *
 * @author Frank Wang
 * @since May 30, 2012
 */
public class BookServiceFactory {

	private static BookService instance;

	public synchronized static BookService getInstance() {
		if ( instance == null )
			instance = new BookServiceImpl();
		return instance;
	}
}
