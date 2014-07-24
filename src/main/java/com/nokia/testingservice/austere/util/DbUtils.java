package com.nokia.testingservice.austere.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.nokia.testingservice.austere.model.DbMeta;

public class DbUtils {

	public static enum DBType{
		sqlserver, mysql, oracle;
		
		public static DBType parse( String str ) {
			for( DBType type : DBType.values() ) {
				if(type.name().equalsIgnoreCase( str )) {
					return type;
				}
			}
			return sqlserver;
		}
		
		public String getDriverClass() {
			switch( this ) {
				case mysql:
					return DRIVER_CLASS_MYSQL;
				case oracle:
					return DRIVER_CLASS_ORACLE;
				default:
					return DRIVER_CLASS_SQLSERVER;
			}
		}
	}
	
	public final static String DRIVER_CLASS_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public final static String DRIVER_CLASS_MYSQL = "com.mysql.jdbc.Driver";
	public final static String DRIVER_CLASS_ORACLE = "oracle.jdbc.driver.OracleDriver";
	public final static String DEFAULT_MONITOR_INSTANCE_NAME = "MTBF_MONITOR";

	public static Map<String, DbMeta> databases = new HashMap<String, DbMeta>();
	public static Collection<String> instances = new ArrayList<String>();
	public static Map<String, String> mapping = new HashMap<String, String>();

	static {
		refreshDB();
		initialMapping();
	}

	public static DBType getDBType( Connection conn ) throws SQLException {
		if(conn==null)
			throw new SQLException("Connection is NULL.");
		String metaClass = conn.getMetaData().getClass().getName().toLowerCase();
		for( DBType type : DBType.values() ) {
			if(metaClass.contains( type.name() ))
				return type;
		}
		throw new SQLException("Unknow Database Connection:"+ metaClass);
	}
	
	public static void removeInstanceName( String instanceName, boolean cascade ) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		try {
			conn = getCentralConnection();
			conn.setAutoCommit( false );
			if( cascade ) {
				
				ps = conn.prepareStatement( "delete from dbinstanceNames where instanceName=?" );
				ps.setString( 1, instanceName );
				ps.executeUpdate();
				ps2 = conn.prepareStatement( "delete from products where instanceName=?" );
				ps2.setString( 1, instanceName );
				ps2.executeUpdate();
				
			}else {
				ps = conn.prepareStatement( "delete from dbinstanceNames where instanceName=?" );
				ps.setString( 1, instanceName );
				ps.executeUpdate();
				ps2 = conn.prepareStatement( "update products set instanceName='' where instanceName=?" );
				ps2.setString( 1, instanceName );
				ps2.executeUpdate();
			}
			conn.commit();
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.setCommit( conn, true );
			CommonUtils.closeQuitely( conn );
		}
		initialMapping();
	}
	
	public static void updateInstanceName( String oldName, String newName ) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		try {
			conn = getCentralConnection();
			conn.setAutoCommit( false );
			ps = conn.prepareStatement( "update dbinstanceNames set instanceName=? where instanceName=?" );
			ps.setString( 1, newName );
			ps.setString( 2, oldName );
			ps.executeUpdate();
			ps2 = conn.prepareStatement( "update products set instanceName=? where instanceName=?" );
			ps2.setString( 1, newName );
			ps2.setString( 2, oldName );
			ps2.executeUpdate();
			conn.commit();
		}catch( SQLException e ) {
			CommonUtils.rollback( conn );
			throw e;
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.setCommit( conn, true );
			CommonUtils.closeQuitely( conn );
		}
		initialMapping();
	}
	
	public static void addInstanceName( String instanceName ) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getCentralConnection();
			ps = conn.prepareStatement( "insert into dbinstanceNames(instanceName) values(?)" );
			ps.setString( 1, instanceName );
			ps.executeUpdate();
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		initialMapping();
	}
	
	public static void registerNewDatabase( Connection conn, DbMeta dm ) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement( "insert into dbo.databases(siteName,url,username,password,driverClass) values(?,?,?,?,?)" );
			ps.setString( 1, dm.getSiteName() );
			ps.setString( 2, dm.getUrl() );
			ps.setString( 3, dm.getUser() );
			ps.setString( 4, dm.getPassword() );
			ps.setString( 5, dm.getDriverClass() );
			ps.executeUpdate();
		} catch ( SQLException e ) {
			LogUtils.getServiceLog().error( "Register new database failed,dm=" + dm.toString(), e );
			throw e;
		} finally {
			CommonUtils.closeQuitely( ps );
		}
	}
	
	public static void removeDatabase( Connection conn, DbMeta dm ) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement( "delete from dbo.databases where siteName=? and url=? and username=? and password=? and driverClass=?" );
			ps.setString( 1, dm.getSiteName() );
			ps.setString( 2, dm.getUrl() );
			ps.setString( 3, dm.getUser() );
			ps.setString( 4, dm.getPassword() );
			ps.setString( 5, dm.getDriverClass() );
			ps.executeUpdate();
		} catch ( SQLException e ) {
			LogUtils.getServiceLog().error( "Remove database failed,dm=" + dm.toString(), e );
			throw e;
		} finally {
			CommonUtils.closeQuitely( ps );
		}
	}

	private static void initialMapping() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getCentralConnection();
			ps = conn.prepareStatement( "select * from DbInstanceNames" );
			rs = ps.executeQuery();
			instances.clear();
			while ( rs.next() ) {
				instances.add( rs.getString( 1 ) );
			}
			rs.close();
			ps.close();
			ps = conn.prepareStatement( "select ProductName,InstanceName from Products group by ProductName,InstanceName" );
			rs = ps.executeQuery();
			mapping.clear();
			while ( rs.next() ) {
				mapping.put( rs.getString( 1 ), rs.getString( 2 ) );
			}
		} catch ( Exception e ) {
			LogUtils.getDbLog().error( "Initial DbUtils Failed.", e );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
	}

	/**
	 * Get central DB connection
	 * 
	 * @return Connection from central DB.
	 */
	public static Connection getCentralConnection() {
		Connection conn = null;
		Throwable t = null;
		try {
			conn = getJndiCentralConnection();
			if ( conn == null )
				conn = getJdbcCentralConnection();
		} catch ( Exception e ) {
			t = e;
		}
		if ( conn == null )
			LogUtils.getDbLog().error( "Get Central Connection failed.", t );
		return conn;
	}

	public static Connection getJndiCentralConnection() throws Exception {
		return getJndiConnection( CommonUtils.getProperties( "db.central.dsname", "db.properties" ) );
	}

	public static Connection getJdbcCentralConnection() {
		Connection conn = null;
		try {
			Class.forName( CommonUtils.getProperties( "db.central.driver", "db.properties" ) );
			conn = DriverManager.getConnection( CommonUtils.getProperties( "db.central.url", "db.properties" ), CommonUtils.getProperties( "db.central.user", "db.properties" ),
					CommonUtils.getProperties( "db.central.password", "db.properties" ) );
		} catch ( Exception e ) {
			LogUtils.getDbLog().error( "Get local Connection failed.", e );
		}
		return conn;
	}

	public static void refreshDB() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			databases.clear();
			conn = getCentralConnection();
			ps = conn.prepareStatement( "select * from dbo.databases order by siteName" );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				DbMeta dm = new DbMeta();
				dm.setSiteName( rs.getString( "siteName" ) );
				dm.setUrl( rs.getString( "url" ) );
				dm.setUser( rs.getString( "username" ) );
				dm.setPassword( rs.getString( "password" ) );
				dm.setDriverClass( rs.getString( "driverClass" ) );
				databases.put( dm.getSiteName(), dm );
			}
		} catch ( Exception e ) {
			LogUtils.getDbLog().error( "Refresh database info failed.", e );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
	}

	public static Map<String, Map<String, Connection>> getConnections() {
		Map<String, Map<String, Connection>> conns = new HashMap<String, Map<String, Connection>>();
		for ( DbMeta dm : databases.values() ) {
			String siteName = dm.getSiteName();
			conns.put( siteName, new HashMap<String, Connection>() );
			for ( String instanceName : instances ) {
				Connection conn = getConnectionByInstance( dm, instanceName );
				if ( conn != null )
					conns.get( siteName ).put( instanceName, conn );
				else
					LogUtils.getDbLog().error( "Get Connection failed.siteName=" + siteName + ",instanceName=" + instanceName );
			}
		}
		return conns;
	}
	
	public static Map<String, Connection> getMonitorConnections() {
		Map<String, Connection> conns = new HashMap<String, Connection>();
		for ( DbMeta dm : databases.values() ) {
			String siteName = dm.getSiteName();
			Connection conn = getMonitorConnection( siteName );
			if ( conn != null )
				conns.put( siteName, conn );
			else
				LogUtils.getDbLog().error( "Get MonitorConnections failed.siteName=" + siteName );
		}
		return conns;
	}

	/**
	 * Get remote monitor instance;
	 * 
	 * @param siteName
	 * @return
	 */
	public static Connection getMonitorConnection( String siteName ) {
		Connection conn = null;
		DbMeta dm = databases.get( siteName );
		if ( dm == null ) {
			refreshDB();
			dm = databases.get( siteName );
		}
		conn = getConnectionByInstance( dm, DEFAULT_MONITOR_INSTANCE_NAME );
		return conn;
	}
	
	/**
	 * Get remote monitor instance;
	 * 
	 * @param siteName
	 * @return
	 */
	public static Connection getMonitorConnection( DbMeta dm ) {
		Connection conn = null;
		conn = getConnectionByInstance( dm, DEFAULT_MONITOR_INSTANCE_NAME );
		return conn;
	}
	
	public static Map<String, Connection> getConnections( String siteName ) {
		Map<String, Connection> conns = new HashMap<String, Connection>();
		DbMeta dm = databases.get( siteName );
		if ( dm == null ) {
			refreshDB();
			dm = databases.get( siteName );
		}

		for ( String instanceName : instances ) {
			Connection conn = getConnectionByInstance( dm, instanceName );
			if ( conn != null )
				conns.put( instanceName, conn );
			else
				LogUtils.getDbLog().error( "Get Connection failed.siteName=" + siteName + ",instanceName=" + instanceName );
		}
		return conns;
	}
	
	
	
	
	public synchronized static int getNextIdSqlServer( Connection conn, String tableName ) {
		String sql = "SELECT SCOPE_IDENTITY()";
		LogUtils.getDbLog().debug( "[getNextId]" + sql );
		PreparedStatement ps = null;
		ResultSet rs = null;
		int nextId = -1;
		try {
			ps = conn.prepareStatement( sql );
			rs = ps.executeQuery();
			rs.next();
			nextId = rs.getInt(1);
		} catch ( SQLException e ) {
			LogUtils.getDbLog().error( "Get Next Id failed.Tablename=" + tableName, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
		}
		return nextId - 1;
	}
	
	public synchronized static int getNextIdMysql( Connection conn, String tableName ) {
		String sql = "SHOW TABLE STATUS LIKE '" + tableName + "'";
		LogUtils.getDbLog().debug( "[getNextId]" + sql );
		PreparedStatement ps = null;
		ResultSet rs = null;
		int nextId = -1;
		try {
			ps = conn.prepareStatement( sql );
			rs = ps.executeQuery();
			rs.next();
			nextId = rs.getInt( "Auto_increment" );
		} catch ( SQLException e ) {
			LogUtils.getDbLog().error( "Get Next Id failed.Tablename=" + tableName, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
		}
		return nextId - 1;
	}

	public static Connection getConnection( String siteName, String instanceName ) {
		Connection conn = null;
		DbMeta dm = databases.get( siteName );
		if ( dm == null ) {
			refreshDB();
			dm = databases.get( siteName );
		}
		conn = getConnectionByInstance( dm, instanceName );
		if ( conn == null )
			LogUtils.getDbLog().error( "Get Connection failed.siteName=" + siteName + ",instanceName=" + instanceName );
		return conn;
	}

	public static Connection getConnectionByProduct( String siteName, String product ) {
		Connection conn = null;
		DbMeta dm = databases.get( siteName );
		if ( dm == null ) {
			refreshDB();
			dm = databases.get( siteName );
		}
		conn = getConnectionByProduct( dm, product );
		if ( conn == null )
			LogUtils.getDbLog().error( "Get Connection failed.siteName=" + siteName + ",product=" + product );
		return conn;
	}

	private static Connection getConnectionByInstance( DbMeta dm, String instanceName ) {
		Connection conn = null;
		if ( dm != null && instanceName != null && !instanceName.trim().equals( "" ) ) {
			try {
				Class.forName( dm.getDriverClass() );
				conn = DriverManager.getConnection( dm.getUrl() + instanceName, dm.getUser(), dm.getPassword() );
			} catch ( Exception e ) {
				LogUtils.getDbLog().error( "Get Data Connection failed, db=" + dm, e );
			}
		}

		if ( conn == null )
			LogUtils.getDbLog().error( "Get Connection failed.DbMeta=" + dm + ",instanceName=" + instanceName );
		return conn;
	}

	public static String getInstanceName( String product ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String instanceName = "";
		try {
			conn = getCentralConnection();
			ps = conn.prepareStatement( "select instanceName from Products where ProductName=?" );
			ps.setString( 1, product );
			rs = ps.executeQuery();
			rs.next();
			instanceName = rs.getString( 1 );
		} catch ( Exception e ) {
			LogUtils.getDbLog().error( "get InstanceName failed. Product=" + product );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return instanceName;
	}

	private static Connection getConnectionByProduct( DbMeta dm, String product ) {
		Connection conn = null;
		if ( dm != null && product != null && !product.trim().equals( "" ) ) {
			try {
				Class.forName( dm.getDriverClass() );
				conn = DriverManager.getConnection( dm.getUrl() + getInstanceName( product ), dm.getUser(), dm.getPassword() );
			} catch ( Exception e ) {
				LogUtils.getDbLog().error( "Get Data Connection failed, db=" + dm, e );
			}
		}

		if ( conn == null )
			LogUtils.getDbLog().error( "Get Connection failed.DbMeta=" + dm + ",product=" + product );
		return conn;
	}

	public static Connection getJndiConnection( String dsName ) {
		Connection conn = null;
		try {
			Context initContext = new InitialContext();
			Context envContext = ( Context ) initContext.lookup( "java:/comp/env" );
			DataSource ds = ( DataSource ) envContext.lookup( dsName );
			conn = ds.getConnection();
		} catch ( Exception e ) {
			LogUtils.getDbLog().error( "Get Jndi Connection failed,dsName=" + dsName, e );
		}
		return conn;
	}
}
