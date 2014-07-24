package com.nokia.testingservice.austere;

import junit.framework.Assert;

import org.junit.Test;

import com.nokia.testingservice.austere.util.DesUtils;

public class DesUtilTest {

	@Test
	public void testEncrptAndDecrpt() {
		String str = "evachen";
		String en = DesUtils.encrypt( str );
		String de = DesUtils.decrypt( en );
		Assert.assertEquals( de, str );
	}
}
