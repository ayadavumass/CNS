package edu.umass.cs.contextservice.test;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import edu.umass.cs.contextservice.utils.Utils;

/**
 * Test for latin-1 encoding used by databases, especically mysql and
 * mysqldump. ISO-8859-1 is the official name for latin-1 encoding
 * 
 * @author ayadav
 *
 */
public class EncodingTest 
{
	public static void main(String[] args) throws UnsupportedEncodingException
	{
		Random rand = new Random();
		byte[] randArr = new byte[20];
		rand.nextBytes(randArr);
		
		System.out.println("Hex form "+Utils.byteArrayToHex(randArr));
		//String s = "Hello World! \u00ff";
		//byte[] b = s.getBytes("ISO-8859-1");
		String latinCode = new String(randArr, "ISO-8859-1");
		System.out.println("String in latin1 encding "+latinCode);
		
		byte[] byteArr = latinCode.getBytes("ISO-8859-1");
		
		System.out.println("Hex form "+Utils.byteArrayToHex(byteArr));
	}
}