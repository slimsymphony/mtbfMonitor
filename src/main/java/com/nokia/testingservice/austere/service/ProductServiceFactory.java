package com.nokia.testingservice.austere.service;

/**
 * Factory for product service.
 *
 * @author Frank Wang
 * @since Jun 5, 2012
 */
public class ProductServiceFactory {

	private static ProductService instance;
	
	public synchronized static ProductService getInstance() {
		if(instance==null)
			instance = new ProductServiceImpl();
		return instance;
	}
}
