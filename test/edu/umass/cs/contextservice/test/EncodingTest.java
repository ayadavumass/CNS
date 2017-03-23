package edu.umass.cs.contextservice.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Random;

import edu.umass.cs.contextservice.config.ContextServiceConfig;
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
	public static final String LATIN_ENCODING = "UTF8";//"ISO-8859-1"
	
	
	public static void main(String[] args) throws UnsupportedEncodingException
	{
		Random rand = new Random();
		byte[] randArr = new byte[20];
		rand.nextBytes(randArr);
		
		System.out.println("Hex form "+Utils.byteArrayToHex(randArr));
		//String s = "Hello World! \u00ff";
		//byte[] b = s.getBytes("ISO-8859-1");
		String latinCode = new String(randArr, LATIN_ENCODING);
		System.out.println("String in latin1 encding "+latinCode);
		
		byte[] byteArr = latinCode.getBytes(LATIN_ENCODING);
		
		System.out.println("Hex form "+Utils.byteArrayToHex(byteArr));
		
		
		BufferedWriter bw = null;
		BufferedReader br = null;
		
		try
		{
			bw = new BufferedWriter(
		    		new OutputStreamWriter(new FileOutputStream("output"),
		    				LATIN_ENCODING));
			
			for(int i=0; i<10; i++)
			{
				rand.nextBytes(randArr);
				
				System.out.println("Write Hex form "+Utils.byteArrayToHex(randArr));
				//String s = "Hello World! \u00ff";
				//byte[] b = s.getBytes("ISO-8859-1");
				latinCode = new String(randArr, LATIN_ENCODING);
				//System.out.println("String in latin1 encding "+latinCode);
				
				bw.write(latinCode+"\n");
				//byte[] byteArr = latinCode.getBytes("ISO-8859-1");
				
				//System.out.println("Hex form "+Utils.byteArrayToHex(byteArr));
			}
			
			bw.close();
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream("output"),
					LATIN_ENCODING));
			
			String currLine = "";
			while( (currLine = br.readLine()) != null )
			{
				byteArr = currLine.getBytes(LATIN_ENCODING);
				
				System.out.println("Read Hex form "+Utils.byteArrayToHex(byteArr));
			}
		}
		catch(IOException ioex)
		{
			ioex.printStackTrace();
		}
		finally
		{
			if(bw != null)
			{
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(br != null)
			{
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		try 
		{
			File fileDir = new File("output");
			
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileDir), "UTF8"));
			
			out.append("Website UTF-8").append("\n");
			out.append("?? UTF-8").append("\n");
			out.append("??????? UTF-8").append("\n");
				
			out.flush();
			out.close();
		} 
		catch (UnsupportedEncodingException e) 
		{
			System.out.println(e.getMessage());
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		try 
		{
			File fileDir = new File("output");
			BufferedReader in = new BufferedReader(
					new InputStreamReader( new FileInputStream(fileDir), "UTF8"));
			
			String str;
			
			while ((str = in.readLine()) != null) 
			{
				System.out.println(str);
			}
			in.close();
	    }
	    catch (UnsupportedEncodingException e)
	    {
			System.out.println(e.getMessage());
	    }
	    catch (IOException e)
	    {
			System.out.println(e.getMessage());
	    }
	    catch (Exception e)
	    {
			System.out.println(e.getMessage());
	    }
	
	}
}