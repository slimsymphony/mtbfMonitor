package com.nokia.testingservice.austere;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class MailTest {

	/**
	 * @param args
	 * @throws EmailException 
	 */
	public static void main( String[] args ) throws EmailException {
		SimpleEmail mail = new SimpleEmail();
		mail.setCharset( "UTF-8" );
		mail.addTo( "frank.8.wang@nokia.com" );
		mail.setFrom( "Integration-and-Migration-Group@nokia.com" );
		mail.setSmtpPort( 25 );
		mail.setHostName( "smtp.nokia.com" );
		mail.setSubject( "Welcome!" );
		mail.setMsg( "Hi Evan,\n    Welcome join MSFT! This is your new Email address: evan.1.chen@microsoft.com \n    If you have any questions, please don't hesitate to contact us!\n    Enjoy your new journey!\nBest Regards\nIntegration-and-Migration-Group" );
		mail.send();
	}

}
