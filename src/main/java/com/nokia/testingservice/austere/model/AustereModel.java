package com.nokia.testingservice.austere.model;

import com.nokia.testingservice.austere.util.CommonUtils;

/**
 * Base class for all the Model classes. Provide toString, clone features.
 * 
 * @author Frank Wang
 * @since May 30, 2012
 */
public class AustereModel<T extends AustereModel<?>> implements Cloneable {

	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}

	@SuppressWarnings( "unchecked" )
	public T clone() {
		T o = null;
		try {
			o = (T)super.clone();
		} catch ( CloneNotSupportedException e ) {
			e.printStackTrace();
		}
		return o;
	}
}
