package edu.umass.cs.contextservice.attributeInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.contextservice.config.ContextServiceConfig;
import edu.umass.cs.contextservice.queryparsing.ProcessingQueryComponent;

/**
 * Defines the attribute types on which context based
 * communication is supported.
 * @author ayadav
 */
public class AttributeTypes
{
	// this is the largest ascii char.
	// it is used in generating largest string of length n
	// its ascii value is 126
	public static final char largestAsciiChar = '~';
	
	// smallest printable ascii character is space.
	// if attributeInfo specifies minVal to be zero
	// then smallest String is "", otherwise if minVal is 
	// 5, then smallest string is space 5 times"     ".
	public static final char smallestAsciiChar = ' ';
	
	// maximum string length of String data type supported
	public static final int MAX_STRING_SIZE									= 10000;
	
	public static final String IntType 										= "Integer";
	public static final String LongType 									= "Long";
	public static final String DoubleType 									= "Double";
	public static final String StringType 									= "String";
	
	/*public static final HashMap<String, String> mySQLDataType 			= new HashMap<String, String>()
	{
		{
			mySQLDataType.put(IntType, "INTEGER");
			mySQLDataType.put(LongType, "BIGINT");
			mySQLDataType.put(DoubleType, "DOUBLE");
			mySQLDataType.put(StringType, "VARCHAR("+MAX_STRING_SIZE+")");
		}
	};*/
	
	
	public static Map<String, AttributeMetaInfo> attributeMap 				= null;
	public static HashMap<String, String> mySQLDataType 					= null;
	
	/**
	 * checks if the passed value is an attribute or not
	 * @param attribute
	 * @return true if it's a attribute
	 */
	public static boolean checkIfAttribute(String attribute)
	{
		/*if(attributeMap == null)
		{
			initialize();
		}*/
		return attributeMap.containsKey(attribute);
	}
	
	public static void initialize()
	{
		attributeMap 	= new HashMap<String, AttributeMetaInfo>();
		mySQLDataType 	= new HashMap<String, String>();
		
		mySQLDataType.put(IntType, "INTEGER");
		mySQLDataType.put(LongType, "BIGINT");
		mySQLDataType.put(DoubleType, "DOUBLE");
		mySQLDataType.put(StringType, "VARCHAR("+MAX_STRING_SIZE+")");
		
		try 
		{
			readAttributeInfo();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		/*for(int i=0;i<ContextServiceConfig.NUM_ATTRIBUTES;i++)
		{
			AttributeMetaInfo attrInfo 
						= new AttributeMetaInfo("longitude", -180+"", 180+"", -180+"", DoubleType);
			attributeMap.put(ContextServiceConfig.CONTEXT_ATTR_PREFIX+i,
					ContextServiceConfig.CONTEXT_ATTR_PREFIX+i);
		}*/
	}
	
	private static void readAttributeInfo() throws IOException
	{
		FileReader freader 	  = new FileReader(ContextServiceConfig.attributeInfoFileName);
		BufferedReader reader = new BufferedReader( freader );
		String line 		  = null;
		
		while ( (line = reader.readLine()) != null )
		{
			line = line.trim();
			if(line.startsWith("#"))
			{
				// ignore comments
			}
			else
			{
				String [] parsed = line.split(",");
				String attrName = parsed[0].trim();
				String minValue = parsed[1].trim();
				String maxValue = parsed[2].trim();
				String defaultValue = parsed[3].trim();
				String dataType = parsed[4].trim();
				
				
				// to calculate minValue, maxValue in case not directly 
				// given in the file like in String datatype case
				switch(dataType)
				{
					case AttributeTypes.StringType:
					{
						// generate minimum String
						int minStrLen = Integer.parseInt(minValue);
						minValue = AttributeTypes.getMinimumStringOfArgLength(minStrLen);
						
						//generate maximum String
						int maxStrLen = Integer.parseInt(maxValue);
						maxValue = AttributeTypes.getMaximumStringOfArgLength(maxStrLen);
						
						// default value just needs double quotes removal
						String tempDefaultVal = "";
						// removing the double quotes
						for(int i=1;i<defaultValue.length()-1;i++)
						{
							tempDefaultVal = tempDefaultVal + defaultValue.charAt(i);
						}
						defaultValue = tempDefaultVal;
						break;
					}
				}
				
				AttributeMetaInfo attrInfo =
						new AttributeMetaInfo(attrName, minValue, maxValue, defaultValue, dataType);
				
				attributeMap.put(attrInfo.getAttrName(), attrInfo);
			}
		}
		reader.close();
		freader.close();
	}
	
	/**
	 * converts String to given datatype
	 * @return
	 */
	public static Object convertStringToDataType(String value, String dataType)
	{
		switch(dataType)
		{
			case IntType:
			{
				return Integer.parseInt(value);
				//break;
			}
			case LongType:
			{
				return Long.parseLong(value);
				//break;
			}
			case DoubleType:
			{
				return Double.parseDouble(value);
				//break;
			}
			case StringType:
			{
				return value;
				//break;
			}
		}
		return null;
	}
	
	public static Object convertStringToDataTypeForMySQL(String value, String dataType)
	{
		switch(dataType)
		{
			case IntType:
			{
				return Integer.parseInt(value);
				//break;
			}
			case LongType:
			{
				return Long.parseLong(value);
				//break;
			}
			case DoubleType:
			{
				
				return Double.parseDouble(value);
				//break;
			}
			case StringType:
			{
				// mysql needs to have string in single quotes
				return "'"+value+"'";
				//break;
			}
		}
		return null;
	}
	
	public static Vector<DomainPartitionInfo> partitionDomain(int numPartitions, String minValue, String maxValue, String dataType)
	{
		Vector<DomainPartitionInfo> domainParitionInfo = new Vector<DomainPartitionInfo>();
		
		switch(dataType)
		{
			case AttributeTypes.DoubleType:
			{
				double minValD = Double.parseDouble(minValue);
				double maxValD = Double.parseDouble(maxValue);
				
				double numElemementsPerPartition = 
						Math.floor((maxValD - minValD)/numPartitions);
				double currLower = minValD;
				double currUpper = minValD;
				
				for(int j=0;j<numPartitions;j++)
				{
					currLower = currUpper;
					currUpper = currLower + numElemementsPerPartition;
					
					if( currUpper > maxValD )
						currUpper = maxValD;
					
					DomainPartitionInfo partitionInfo = new DomainPartitionInfo(j, currLower+"", currUpper+"");
					domainParitionInfo.add(partitionInfo);
				}	
				break;
			}
			case AttributeTypes.IntType:
			{
				int minValI = Integer.parseInt(minValue);
				int maxValI = Integer.parseInt(maxValue);
				
				int numElemementsPerPartition = 
							(int)Math.floor((maxValI - minValI)/numPartitions);
				int currLower = minValI;
				int currUpper = minValI;
				
				for(int j=0;j<numPartitions;j++)
				{
					currLower = currUpper;
					currUpper = currLower + numElemementsPerPartition;
					
					if( currUpper > maxValI )
						currUpper = maxValI;
					
					DomainPartitionInfo partitionInfo = new DomainPartitionInfo(j, currLower+"", currUpper+"");
					domainParitionInfo.add(partitionInfo);
				}
				break;
			}
			case AttributeTypes.LongType:
			{
				long minValL = Long.parseLong(minValue);
				long maxValL = Long.parseLong(maxValue);
				
				long numElemementsPerPartition = (long)
						Math.floor((maxValL - minValL)/numPartitions);
				long currLower = minValL;
				long currUpper = minValL;
				
				for(int j=0;j<numPartitions;j++)
				{
					currLower = currUpper;
					currUpper = currLower + numElemementsPerPartition;
					
					if( currUpper > maxValL )
						currUpper = maxValL;
					
					DomainPartitionInfo partitionInfo = new DomainPartitionInfo(j, currLower+"", currUpper+"");
					domainParitionInfo.add(partitionInfo);
				}
				break;
			}
			case AttributeTypes.StringType:
			{
				// first get a random string length
				// then get numPartitions random strings
				// that partitions space in numPartitions
				// same seed is needed for each node
				Random rand = new Random(0);
				String[] partitionStrings = new String[numPartitions-1];
				
				HashMap<String, Boolean> toGetUniquePartitions 
											= new HashMap<String, Boolean>();
				
				// gives us (numPartitions-1) partition points
				while( toGetUniquePartitions.size() != (numPartitions-1) )
				{
					// length should be at least one
					int currStrLen = 1+rand.nextInt(maxValue.length()-1);
					
					String randomString = 
							RandomStringUtils.random(currStrLen, 0, 0, true, true, null, rand).toLowerCase();
					//String randomString = RandomStringUtils.randomAlphanumeric(currStrLen).toLowerCase();
					toGetUniquePartitions.put(randomString, true);
				}
				partitionStrings = toGetUniquePartitions.keySet().toArray(partitionStrings);
				
				Arrays.sort(partitionStrings);
				
				
				// now partitionStrings is sorted so we just partition domain in this order.
				
				String currLower = minValue;
				String currUpper = minValue;
				
				for(int j=0;j<(numPartitions-1);j++)
				{
					currLower = currUpper;
					currUpper =  partitionStrings[j];
					
					DomainPartitionInfo partitionInfo = new DomainPartitionInfo(j, currLower+"", currUpper+"");
					domainParitionInfo.add(partitionInfo);
				}
				
				if( (numPartitions-2) >=0 )
				{
					currLower = partitionStrings[numPartitions-2];
					currUpper =  maxValue;
					
					DomainPartitionInfo partitionInfo = new DomainPartitionInfo(numPartitions-1, currLower+"", currUpper+"");
					domainParitionInfo.add(partitionInfo);
				}
				
				break;
			}
		}
		for(int i=0;i<domainParitionInfo.size();i++)
		{
			DomainPartitionInfo dinfo = domainParitionInfo.get(i);
			//System.out.println("DomainPartitionInfo dinfo "+dinfo.lowerbound+" "+dinfo.upperbound+" "+dinfo.partitionNum);
		}
		return domainParitionInfo;
	}
	
	/**
	 * returns next String in lexicographic order
	 * @return
	 */
	public static String getNextStringInLexicographicOrder(String inputStr)
	{
		boolean allTilde = true;
		
		char[] charArray = inputStr.toCharArray();
		int strIndex = charArray.length-1;
		
		while(strIndex >= 0)
		{
			//char charAtIndex = inputStr.charAt(strIndex);
			if(charArray[strIndex] < largestAsciiChar)
			{
				charArray[strIndex] = (char)(charArray[strIndex] + 1);
				allTilde = false;
				break;
			}
			strIndex--;
		}
		
		if(allTilde)
		{
			inputStr = inputStr + smallestAsciiChar;
			return inputStr;
		}
		else
		{
			return  new String(charArray);
		}
	}
	
	/**
	 * returns a minimum string of the specified length
	 * @param strLength
	 * @return
	 */
	public static String getMinimumStringOfArgLength(int strLength)
	{
		String retString = "";
		
		for(int i=0;i<strLength;i++)
		{
			retString = retString+smallestAsciiChar;
		}
		return retString;
	}
	
	/**
	 * returns a minimum string of the specified length
	 * @param strLength
	 * @return
	 */
	public static String getMaximumStringOfArgLength(int strLength)
	{
		String retString = "";
		
		for(int i=0;i<strLength;i++)
		{
			retString = retString+largestAsciiChar;
		}
		return retString;
	}
	
	
	/**
	 * compared attValue if it satisfies the given processing component
	 * @param pqc
	 * @param attrValueJSON
	 * @return
	 */
	public static boolean checkForComponent(ProcessingQueryComponent pqc, JSONObject attrValueJSON)
	{
		String attrName = pqc.getAttributeName();
		
		String dataType = attributeMap.get(attrName).getDataType();
		String valueToCheck;
		try {
			valueToCheck = attrValueJSON.getString(attrName);
		} catch (JSONException e)
		{
			e.printStackTrace();
			return false;
		}
		boolean retValue = false;
		switch(dataType)
		{
			case AttributeTypes.DoubleType:
			{
				double lowerBoundD = Double.parseDouble(pqc.getLowerBound());
				double upperBoundD = Double.parseDouble(pqc.getUpperBound());
				
				double valueD = Double.parseDouble(valueToCheck);
				
				if(lowerBoundD != upperBoundD)
				{
					if( (valueD >= lowerBoundD) && (valueD < upperBoundD) )
					{
						retValue = true;
					}
				}
				else
				{
					if(valueD == lowerBoundD)
					{
						retValue = true;
					}
				}
				break;
			}
			case AttributeTypes.IntType:
			{
				int lowerBoundI = Integer.parseInt(pqc.getLowerBound());
				int upperBoundI = Integer.parseInt(pqc.getUpperBound());
				
				int valueI = Integer.parseInt(valueToCheck);
				
				if(lowerBoundI != upperBoundI)
				{
					if( (valueI >= lowerBoundI) && (valueI < upperBoundI) )
					{
						retValue = true;
					}
				}
				else
				{
					if(valueI == lowerBoundI)
					{
						retValue = true;
					}
				}
				
				break;
			}
			case AttributeTypes.LongType:
			{
				long lowerBoundL = Long.parseLong(pqc.getLowerBound());
				long upperBoundL = Long.parseLong(pqc.getUpperBound());
				
				long valueL = Long.parseLong(valueToCheck);
				
				if(lowerBoundL != upperBoundL)
				{
					if( (valueL >= lowerBoundL) && (valueL < upperBoundL) )
					{
						retValue = true;
					}
				}
				else
				{
					if(valueL == lowerBoundL)
					{
						retValue = true;
					}
				}
				break;
			}
			case AttributeTypes.StringType:
			{
				String lowerBoundS = pqc.getLowerBound();
				String upperBoundS = pqc.getUpperBound();
				
				String valueS = valueToCheck;
				
				if(!lowerBoundS.equals(upperBoundS))
				{
					if( (valueS.compareTo(lowerBoundS) >=0 ) && (valueS.compareTo(upperBoundS) < 0) )
					{
						retValue = true;
					}
				}
				else
				{
					if(valueS.equals(lowerBoundS))
					{
						retValue = true;
					}
				}
				break;
			}
		}
		return retValue;
	}
	
	public static class DomainPartitionInfo
	{
		public final int partitionNum;
		public final String lowerbound;
		public final String upperbound;
		
		public DomainPartitionInfo( int partitionNum, String lowerbound, String upperbound )
		{
			this.partitionNum = partitionNum;
			this.lowerbound = lowerbound;
			this.upperbound = upperbound;
		}
	}
	
	public static Vector<String> getAllAttributes()
	{
		Vector<String> attributes = new Vector<String>();
		attributes.addAll(attributeMap.keySet());
		return attributes;
	}
	
	public static void main(String [] args)
	{
		String s1 = "~~~~ ";
		String s2 = "~~~~a";
		int diff = s1.compareTo(s2);
		System.out.println("diff "+diff);
		char newChar = (char)(smallestAsciiChar+1);
		System.out.println("char "+smallestAsciiChar+" int char "+(int)smallestAsciiChar+" plus1 char"+newChar+" +1 int "
		+(int)newChar);
		
		System.out.println("lexiconext aaaa next"+getNextStringInLexicographicOrder("aaaa"));
		System.out.println("lexiconext empty next"+getNextStringInLexicographicOrder(""));
		System.out.println("lexiconext ~~~~~ next "+getNextStringInLexicographicOrder("~~~~~"));
		System.out.println("lexiconext ~~~~a next "+getNextStringInLexicographicOrder("~~~~a"));
		
		//test the string partition domain function
		
		Vector<DomainPartitionInfo> vect = partitionDomain(2, 0+"", 5+"", "String");
		for(int i=0;i<vect.size();i++)
		{
			System.out.println(" "+vect.get(i).partitionNum+" "+vect.get(i).lowerbound
					+" "+vect.get(i).upperbound);
		}
	}
}