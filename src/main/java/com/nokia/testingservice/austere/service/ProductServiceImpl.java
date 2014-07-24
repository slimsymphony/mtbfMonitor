package com.nokia.testingservice.austere.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.Product;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.DbUtils;
import com.nokia.testingservice.austere.util.LogUtils;

public class ProductServiceImpl implements ProductService {

	@Override
	public String[] getProductNames( boolean allProduct ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> products = new ArrayList<String>();
		try {
			String sql = null;
			if ( allProduct )
				sql = "select ProductName from Products order by CreateTime desc";
			else
				sql = "select ProductName from Products where Invalid=0 order by CreateTime desc";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				products.add( rs.getString( 1 ) );
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get product Names failed, allProduct:" + allProduct, ex );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return products.toArray( new String[0] );
	}

	@Override
	public String[] getProductNames( String instanceName, boolean allProduct ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> products = new ArrayList<String>();
		try {
			String sql = null;
			if ( allProduct )
				sql = "select ProductName from Products where instanceName=? order by CreateTime desc";
			else
				sql = "select ProductName from Products where instanceName=? and Invalid=0 order by CreateTime desc";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setString( 1, instanceName );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				products.add( rs.getString( 1 ) );
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get product Names failed, allProduct:" + allProduct, ex );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return products.toArray( new String[0] );
	}

	@Override
	public Collection<Product> getProducts( boolean allProduct ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Product> products = new ArrayList<Product>();
		try {
			String sql = null;
			if ( allProduct )
				sql = "select ProductName,Invalid,CreateTime,instanceName from Products order by CreateTime desc";
			else
				sql = "select ProductName,Invalid,CreateTime,instanceName from Products where Invalid=0 order by CreateTime desc";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				Product p = new Product();
				p.setProductName( rs.getString( 1 ) );
				p.setInvalid( rs.getInt( 2 ) );
				p.setCreateTime( CommonUtils.gmt2local( rs.getDate( 3 ), TimeZone.getDefault() ) );
				p.setInstanceName( rs.getString( 4 ) );
				products.add( p );
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get product Names failed, allProduct:" + allProduct, ex );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return products;
	}

	@Override
	public Collection<Product> getProducts( String instanceName, boolean allProduct ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Product> products = new ArrayList<Product>();
		try {
			String sql = null;
			if ( allProduct )
				sql = "select ProductName,Invalid,CreateTime from Products where InstanceName=? order by CreateTime desc";
			else
				sql = "select ProductName,Invalid,CreateTime from Products where InstanceName=? and Invalid=0 order by CreateTime desc";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setString( 1, instanceName );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				Product p = new Product();
				p.setProductName( rs.getString( 1 ) );
				p.setInvalid( rs.getInt( 2 ) );
				p.setCreateTime( rs.getDate( 3 ) );
				p.setInstanceName( rs.getString( 4 ) );
				products.add( p );
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get product Names failed, allProduct:" + allProduct, ex );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return products;
	}

	@Override
	public void addProduct( String productName, String instanceName ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String sql = "insert into Products(ProductName,Invalid, instanceName,CreateTime) values(?,0,?,?)";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setString( 1, productName );
			ps.setString( 2, instanceName );
			ps.setTimestamp( 3, CommonUtils.local2gmt( new Timestamp(System.currentTimeMillis()), TimeZone.getDefault() ) );
			ps.executeUpdate();
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Add product failed, productName:" + productName + ",instanceName:" + instanceName, ex );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
	}

	@Override
	public void updateProduct( String productName, boolean valid, String instanceName ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql;
		try {
			if ( valid )
				sql = "update Products set Invalid=0,InstanceName=? where ProductName=?";
			else
				sql = "update Products set Invalid=1,InstanceName=? where ProductName=?";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setString( 1, instanceName );
			ps.setString( 2, productName );
			ps.executeUpdate();
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Disable product failed, productName:" + productName, ex );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
	}

	@Override
	public void delProduct( String productName ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String sql = "delete from Products where ProductName=?";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setString( 1, productName );
			ps.executeUpdate();
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Delete product failed, productName:" + productName, ex );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
	}

	@Override
	public List<String> getCurrentProducts( String siteName ) {
		List<String> products = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getMonitorConnection( siteName );
			String sql = "select distinct product from executionStates order by product";
			ps = conn.prepareStatement( sql );
			rs = ps.executeQuery();
			while(rs.next()) {
				products.add( rs.getString( 1 ) );
			}
		}catch(Exception e) {
			LogUtils.getServiceLog().error( "Get Products error", e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return products;
	}

}
