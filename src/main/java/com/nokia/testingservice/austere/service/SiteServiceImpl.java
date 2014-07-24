package com.nokia.testingservice.austere.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.DbMeta;
import com.nokia.testingservice.austere.model.Site;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.DbUtils;
import com.nokia.testingservice.austere.util.LogUtils;

public class SiteServiceImpl implements SiteService {

	@Override
	public void addSite( Site site ) throws ServiceException {
		if ( site == null )
			throw new ServiceException( "Site Object is null." );
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getCentralConnection();
			conn.setAutoCommit( false );
			ps = conn.prepareStatement( "insert into sites(sitename) values(?)" );
			ps.setString( 1, site.getSiteName() );
			ps.executeUpdate();
			for ( DbMeta dm : site.getDatabases() ) {
				DbUtils.registerNewDatabase( conn, dm );
			}
			conn.commit();
		} catch ( Exception e ) {
			CommonUtils.rollback( conn );
			throw new ServiceException( "Add site failed, site=" + site, e );
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.setCommit( conn, true );
			CommonUtils.closeQuitely( conn );
		}
		DbUtils.refreshDB();
	}

	@Override
	public void delSite( String siteName ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getCentralConnection();
			conn.setAutoCommit( false );
			for ( DbMeta dm : this.getDatabaseBySite( siteName ) ) {
				DbUtils.removeDatabase( conn, dm );
			}
			ps = conn.prepareStatement( "delete from sites where siteName=?" );
			ps.setString( 1, siteName );
			ps.executeUpdate();
			conn.commit();
		} catch ( Exception e ) {
			CommonUtils.rollback( conn );
			throw new ServiceException( "Remove site info failed, sitename=" + siteName, e );
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.setCommit( conn, true );
			CommonUtils.closeQuitely( conn );
		}
		DbUtils.refreshDB();
	}

	@Override
	public void updateSiteName( String oldName, String newName ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getCentralConnection();
			conn.setAutoCommit( false );
			ps = conn.prepareStatement( "Update sites set sitename=? where sitename=?" );
			ps.setString( 1, newName );
			ps.setString( 2, oldName );
			ps.executeUpdate();
			ps2 = conn.prepareStatement( "update dbo.databases set sitename=? where sitename=?" );
			ps2.setString( 1, newName );
			ps2.setString( 2, oldName );
			ps2.executeUpdate();
			conn.commit();
		} catch ( Exception e ) {
			CommonUtils.rollback( conn );
			throw new ServiceException( "Update Site name from=" + oldName + ",to=" + newName+" failed.", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		DbUtils.refreshDB();
	}

	private DbMeta encap( ResultSet rs ) throws SQLException {
		DbMeta dm = new DbMeta();
		dm.setSiteName( rs.getString( "sitename" ) );
		dm.setUrl( rs.getString( "url" ) );
		dm.setUser( rs.getString( "username" ) );
		dm.setPassword( rs.getString( "password" ) );
		dm.setDriverClass( rs.getString( "driverClass" ) );
		return dm;
	}

	@Override
	public List<Site> getAllSites() {
		List<Site> sites = new ArrayList<Site>();
		Site site = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "select * from dbo.databases order by siteName" );
			ps2 = conn.prepareStatement( "select * from sites" );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				String siteName = rs.getString( "sitename" );
				boolean isAdded = false;
				for ( Site s : sites ) {
					if ( s.getSiteName().equals( siteName ) ) {
						isAdded = true;
						s.getDatabases().add( encap( rs ) );
						break;
					}
				}
				if ( !isAdded ) {
					site = new Site();
					site.setSiteName( siteName );
					site.setDatabases( new ArrayList<DbMeta>() );
					site.getDatabases().add( encap( rs ) );
					sites.add( site );
				}
			}
			rs2 = ps2.executeQuery();
			while(rs2.next()) {
				String sn = rs2.getString( 1 );
				boolean isFound = false;
				for( Site s : sites ) {
					if( s.getSiteName().equals( sn ) ) {
						isFound = true;
						break;
					}
				}
				if( !isFound ) {
					Site s = new Site();
					s.setSiteName( sn );
					s.setDatabases( new ArrayList<DbMeta>() );
					sites.add( s );
				}
			}
		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Get All Sites info failed.", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( rs2 );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( conn );
		}
		return sites;
	}

	@Override
	public Site getSite( String siteName ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Site site = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "select * from dbo.databases where sitename=?" );
			ps.setString( 1, siteName );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				if ( site == null ) {
					site = new Site();
					site.setSiteName( siteName );
					site.setDatabases( new ArrayList<DbMeta>() );
				}
				site.getDatabases().add( encap( rs ) );
			}
		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Get Site info failed.site=" + siteName, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return site;
	}

	@Override
	public List<DbMeta> getDatabaseBySite( String siteName ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<DbMeta> dbs = new ArrayList<DbMeta>();
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "select * from dbo.databases where sitename=?" );
			ps.setString( 1, siteName );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				dbs.add( encap( rs ) );
			}
		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "get Database BySite failed.site=" + siteName, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return dbs;
	}

	@Override
	public void addDatabase( DbMeta dm ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "insert into dbo.databases(sitename,url,username,password,driverClass) values(?,?,?,?,?)" );
			ps.setString( 1, dm.getSiteName() );
			ps.setString( 2, dm.getUrl() );
			ps.setString( 3, dm.getUser() );
			ps.setString( 4, dm.getPassword() );
			ps.setString( 5, dm.getDriverClass() );
			ps.executeUpdate();
		} catch ( Exception e ) {
			throw new ServiceException( "Add new Database failed, dm=" + dm, e );
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		DbUtils.refreshDB();
	}

	@Override
	public void delDatabase( DbMeta dm ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "delete from dbo.databases where sitename=? and url=? and username=? and password=? and driverClass=?" );
			ps.setString( 1, dm.getSiteName() );
			ps.setString( 2, dm.getUrl() );
			ps.setString( 3, dm.getUser() );
			ps.setString( 4, dm.getPassword() );
			ps.setString( 5, dm.getDriverClass() );
			ps.executeUpdate();
		} catch ( Exception e ) {
			throw new ServiceException( "Delete a database failed, dm=" + dm, e );
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		DbUtils.refreshDB();
	}

	@Override
	public void updateDatabase( DbMeta oldDb, DbMeta newDb ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "update dbo.databases set sitename=? , url=? , username=? , password=? , driverClass=? where sitename=? and url=? and username=? and password=? and driverClass=?" );
			ps.setString( 1, newDb.getSiteName() );
			ps.setString( 2, newDb.getUrl() );
			ps.setString( 3, newDb.getUser() );
			ps.setString( 4, newDb.getPassword() );
			ps.setString( 5, newDb.getDriverClass() );
			ps.setString( 6, oldDb.getSiteName() );
			ps.setString( 7, oldDb.getUrl() );
			ps.setString( 8, oldDb.getUser() );
			ps.setString( 9, oldDb.getPassword() );
			ps.setString( 10, oldDb.getDriverClass() );
			ps.executeUpdate();
		} catch ( Exception e ) {
			throw new ServiceException( "update Database from "+oldDb+" to "+newDb+" failed.", e );
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		DbUtils.refreshDB();
	}

}
