package com.nokia.testingservice.austere;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import oracle.jdbc.driver.OracleDriver;

import com.nokia.testingservice.austere.util.DbUtils;

public class DbTest {
	public static void main(String[] args) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Class.forName( "com.mysql.jdbc.Driver" );
			Class.forName( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
			Class.forName( OracleDriver.class.getCanonicalName() );
			//conn = DriverManager.getConnection( "jdbc:mysql://3CNL12096.noe.nokia.com:3306/dbo?characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false","root","root" );
			//conn = DriverManager.getConnection( "jdbc:sqlserver://3cnd03922:1433;databaseName=AustereCentral","austere","austere2" );
			conn = DriverManager.getConnection( "jdbc:oracle:thin:CI_HUDSON/hudson@saepcidb01.europe.nokia.com:1521:s40ci","CI_HUDSON","hudson" );
			//
			if(conn!=null) {
				System.out.println(DbUtils.getDBType( conn ));
				conn.close();
			} else {
				conn = DriverManager.getConnection( "jdbc:mysql://3CNL12096.noe.nokia.com:3306/dbo?characterEncoding=utf8&user=root&password=password" );
				System.out.println(DbUtils.getDBType( conn ));
				conn.close();
			}
			conn = DbUtils.getCentralConnection();
			System.out.println(DbUtils.getDBType( conn ));
			ps = conn.prepareStatement( "select count(1) from Users" );
			rs = ps.executeQuery();
			rs.next();
			System.out.println("Users :"+rs.getInt( 1 ));
			rs.close();
			ps.close();
			conn.close();
			//conn = DbUtils.getConnection();
			conn = DbUtils.getConnection( "Test", "S40_TB112_TEST_DEV" );
//			ps = conn.prepareStatement("select * from System");
//			rs = ps.executeQuery();
//			ResultSetMetaData meta = rs.getMetaData();
//			int cnt = meta.getColumnCount();
//			System.out.printf(" There are %d columns in this table.\n", cnt);
//			while (rs.next()) {
//				System.out.printf("%s = %d", "SystemID", rs.getInt("SystemID"));
//				System.out.printf("%s = %s", "TestSystem", rs.getString("TestSystem"));
//				System.out.printf("%s = %s", "PlaceID", rs.getString("PlaceID"));
//				System.out.printf("%s = %s", "PCHostname", rs.getString("PCHostname"));
//				System.out.printf("%s = %s", "TestSWver", rs.getString("TestSWver"));
//				System.out.printf("%s = %d", "TCdB", rs.getInt("TCdB"));
//				System.out.printf("%s = %s", "SettingsFile", rs.getString("SettingsFile"));
//				System.out.printf("%s = %s", "SettingsFilever", rs.getString("SettingsFilever"));
//				System.out.printf("%s = %s", "LogFileName", rs.getString("LogFileName"));
//				System.out.println();
//			}
//			ps.close();
			ps = conn.prepareStatement( "select distinct(ConditionID) from Result" );
			Calendar cal = Calendar.getInstance();
			Date date = new Date();
			cal.setTime( date );
			cal.set( Calendar.HOUR, 0 );
			cal.set( Calendar.MINUTE, 0 );
			cal.set( Calendar.SECOND, 0 );
//			Timestamp start = new Timestamp( cal.getTime().getTime() );
//			ps.setTimestamp( 1, start );
			cal.set( Calendar.HOUR, 23 );
			cal.set( Calendar.MINUTE, 59 );
			cal.set( Calendar.SECOND, 29 );
//			Timestamp end = new Timestamp( cal.getTime().getTime() );
//			ps.setTimestamp( 2, end );
			rs = ps.executeQuery();
			while(rs.next()) {
				System.out.println( rs.getInt( 1 ) );
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
	}
}
