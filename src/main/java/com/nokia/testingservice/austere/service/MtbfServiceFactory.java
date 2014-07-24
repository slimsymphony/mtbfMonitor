package com.nokia.testingservice.austere.service;

public class MtbfServiceFactory {

	private static MtbfService instance;

	public synchronized static MtbfService getInstance() {
		if ( instance == null )
			instance = new MtbfServiceImpl();
		return instance;
	}
}
