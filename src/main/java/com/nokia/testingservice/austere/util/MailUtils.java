package com.nokia.testingservice.austere.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.junit.Test;

public class MailUtils {

	public static List<String> austereOwnerList = new ArrayList<String>();
	static {
		austereOwnerList.add( "di.6.yang@nokia.com" );
		austereOwnerList.add( "frank.8.wang@nokia.com" );
	}
	private static String AUSTERE_ADMIN = "Mtbf-Monitor@nokia.com";

	public static void sendMail( Collection<String> targets, Collection<String> ccs, String topic, String details ) throws EmailException {
		SimpleEmail mail = new SimpleEmail();
		mail.setCharset( "UTF-8" );
		for ( String to : targets ) {
			mail.addTo( to );
		}
		if ( ccs != null )
			for ( String cc : ccs ) {
				mail.addCc( cc );
			}
		mail.setFrom( AUSTERE_ADMIN );
		mail.setHostName( "smtp.nokia.com" );
		mail.setSubject( topic );
		mail.setMsg( details );
		mail.send();
	}

	@Test
	public void testSendMail() throws Exception {
		List<String> tos = new ArrayList<String>();
		List<String> ccs = new ArrayList<String>();
		tos.add( "evan.1.chen@nokia.com" );
		tos.add( "jeffery.zha@nokia.com" );
		sendMail( tos, ccs, "编译发布单元测试，请忽略啊亲!", "编译发布单元测试，请忽略啊亲！" );
	}
}
