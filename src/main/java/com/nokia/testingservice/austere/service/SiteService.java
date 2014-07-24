package com.nokia.testingservice.austere.service;

import java.util.List;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.DbMeta;
import com.nokia.testingservice.austere.model.Site;

public interface SiteService {
	void addSite( Site site ) throws ServiceException;

	void delSite( String siteName ) throws ServiceException;

	void updateSiteName( String oldName, String newName ) throws ServiceException;

	List<Site> getAllSites();

	Site getSite( String siteName );

	List<DbMeta> getDatabaseBySite( String siteName );
	
	void addDatabase( DbMeta dm ) throws ServiceException;
	
	void delDatabase( DbMeta dm ) throws ServiceException;
	
	void updateDatabase( DbMeta oldDb, DbMeta newDb ) throws ServiceException;
}
