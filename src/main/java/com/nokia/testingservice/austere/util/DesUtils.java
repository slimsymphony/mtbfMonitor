package com.nokia.testingservice.austere.util;

import java.security.*;

import javax.crypto.*;

public class DesUtils {

	private static String strDefaultKey = "austere";

	private static Cipher encryptCipher;

	private static Cipher decryptCipher;
	
	static {
		try {
			Security.addProvider( new com.sun.crypto.provider.SunJCE() );
			Key key = getKey( strDefaultKey.getBytes() );
			encryptCipher = Cipher.getInstance( "DES" );
			encryptCipher.init( Cipher.ENCRYPT_MODE, key );
			decryptCipher = Cipher.getInstance( "DES" );
			decryptCipher.init( Cipher.DECRYPT_MODE, key );
		} catch ( Exception e ) {
			e.printStackTrace();
		} 
	}

	/**
	 * Transfer byte array to 16band string, eg.byte[]{8,18} will be transfer to  0813 and this method is reversible for hexStr2ByteArr(String strIn)
	 * 
	 * @param arrB byte array to be transferred
	 * @return string after transfer
	 * @throws Exception 
	 */
	public static String byteArr2HexStr( byte[] arrB ) {
		int iLen = arrB.length;
		StringBuffer sb = new StringBuffer( iLen * 2 );
		for ( int i = 0; i < iLen; i++ ) {
			int intTmp = arrB[i];
			while ( intTmp < 0 ) {
				intTmp = intTmp + 256;
			}
			if ( intTmp < 16 ) {
				sb.append( "0" );
			}
			sb.append( Integer.toString( intTmp, 16 ) );
		}
		return sb.toString();
	}

	/**
	 * Transfer 16band string to byte array, this method is reversible for byteArr2HexStr(byte[] arrB)
	 * 
	 * @param strIn String to be transferred
	 * @return byte array after been transferred
	 * @throws Exception 
	 */
	public static byte[] hexStr2ByteArr( String strIn ) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;
		byte[] arrOut = new byte[iLen / 2];
		for ( int i = 0; i < iLen; i = i + 2 ) {
			String strTmp = new String( arrB, i, 2 );
			arrOut[i / 2] = ( byte ) Integer.parseInt( strTmp, 16 );
		}
		return arrOut;
	}

	/**
	 * encrypt byte array
	 * 
	 * @param arrB 
	 * @return 
	 * @throws Exception
	 */
	public static byte[] encrypt( byte[] arrB ) throws Exception {
		return encryptCipher.doFinal( arrB );
	}

	/**
	 * encrypt string
	 * 
	 * @param strIn 
	 * @return 
	 * @throws Exception
	 */
	public static String encrypt( String strIn ){
		String after = null;
		try {
			after =  byteArr2HexStr( encrypt( strIn.getBytes() ) );
		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Encrypt password error,"+strIn, e );
		}
		return after;
	}

	/**
	 * decrypt byte array
	 * 
	 * @param arrB 
	 * @return 
	 * @throws Exception
	 */
	public static byte[] decrypt( byte[] arrB ) throws Exception {
		return decryptCipher.doFinal( arrB );
	}

	/**
	 * decrypt String
	 * 
	 * @param strIn 
	 * @return 
	 * @throws Exception
	 */
	public static String decrypt( String strIn ){
		String after = null;
		try {
			after =  new String( decrypt( hexStr2ByteArr( strIn ) ) );
		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Decrypt password error,"+strIn, e );
		}
		return after;
	}

	/**
	 * Generate key from assigned string.
	 * 
	 * @param arrBTmp
	 * @return 
	 * @throws java.lang.Exception
	 */
	private static Key getKey( byte[] arrBTmp ) throws Exception {
		byte[] arrB = new byte[8];
		for ( int i = 0; i < arrBTmp.length && i < arrB.length; i++ ) {
			arrB[i] = arrBTmp[i];
		}
		Key key = new javax.crypto.spec.SecretKeySpec( arrB, "DES" );
		return key;
	}
	
}
