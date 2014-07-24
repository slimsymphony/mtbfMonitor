package com.nokia.testingservice.austere;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.nokia.testingservice.austere.util.LDAPAuthenticator;

public class LdapUtilsTest {
	private LDAPAuthenticator l;
	
	@Before
	public void setup() {
		l = new LDAPAuthenticator();
	}
	
	@Test
	public void testAuth() {
		try {
			String info = LDAPAuthenticator.getEmployeeIdForMail( "evan.1.chen@nokia.com", "europe" );
			Assert.assertEquals( info,"10354237" );
			String uid = LDAPAuthenticator.getUserInfoFromNoe( "evachen", "uid" );
			Assert.assertEquals( uid,"evachen" );
			String mail = LDAPAuthenticator.getUserInfoFromNoe( "evachen", "mail" );
			Assert.assertEquals( mail,"evan.1.chen@nokia.com" );
			String cn = LDAPAuthenticator.getUserInfoFromNoe( "evachen", "cn" );
			Assert.assertEquals( cn,"Chen Wei" );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetInfo() {
		Gson gson = new Gson();
		Assert.assertEquals( "{\"ldapInfo\":\"\"}", gson.toJson( l ) );
	}
}
