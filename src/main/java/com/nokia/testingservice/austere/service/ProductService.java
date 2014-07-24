package com.nokia.testingservice.austere.service;

import java.util.Collection;
import java.util.List;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.Product;

/**
 * Service Api for Products.
 * 
 * @author Frank Wang
 * @since Jun 5, 2012
 */
public interface ProductService {

	/**
	 * Get all product names.
	 * 
	 * @param allProduct if true,get all products including invalid products.
	 * @return
	 */
	public String[] getProductNames( boolean allProduct );
	
	public String[] getProductNames(String instanceName,  boolean allProduct );
	
	public Collection<Product> getProducts( boolean allProduct );
	
	public Collection<Product> getProducts( String instanceName, boolean allProduct );

	public void addProduct( String productName, String instanecName ) throws ServiceException;
	
	public void updateProduct( String productName, boolean valid, String instanceName ) throws ServiceException;
	
	public void delProduct( String productName ) throws ServiceException;
	
	public List<String> getCurrentProducts( String siteName ) ;
}
