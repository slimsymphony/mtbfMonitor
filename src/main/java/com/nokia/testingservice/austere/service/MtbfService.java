package com.nokia.testingservice.austere.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.ReportMeta;
import com.nokia.testingservice.austere.model.MTBFDetail;
import com.nokia.testingservice.austere.model.MtbfData;

public interface MtbfService {

	Collection<MTBFDetail> getMtbfDetails( String siteName, String product );

	Collection<MTBFDetail> getMtbfDetails( int date, String siteName, String product );
	
	Collection<MTBFDetail> getWeekMtbfDetails( int week, String siteName, String product );

	int addMTBFDetail( MTBFDetail md ) throws ServiceException;
	
	void updateMTBFDetail( MTBFDetail md ) throws ServiceException;
	
	void deleteMTBFDetail( int seq ) throws ServiceException;
	
	List<ReportMeta> getDailyReportMetas( String siteName, String instanceName, Date date );
	
	List<ReportMeta> getDailyReportMetasByProduct( String siteName, String product, Date date );
	
	List<ReportMeta> getWeeklyReportMetas( String siteName, String instanceName, int week );
	
	List<ReportMeta> getWeeklyReportMetasByProduct( String siteName, String product, int week );
	
	List<MtbfData> transfer2MtbfData( List<ReportMeta> rms );
	
}
