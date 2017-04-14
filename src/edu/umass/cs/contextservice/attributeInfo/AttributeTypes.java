package edu.umass.cs.contextservice.attributeInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import edu.umass.cs.contextservice.regionmapper.helper.AttributeValueRange;

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
	
	
	public static HashMap<String, AttributeMetaInfo> attributeMap 			= null;
	// stores the attributes in order as read from the file.
	// because only storing in hashmap destroys order.
	public static List<String> attributeInOrderList							= null;
	public static HashMap<String, String> mySQLDataType 					= null;
	
	
	/**
	 * checks if the passed value is an attribute or not
	 * @param attribute
	 * @return true if it's a attribute
	 */
	public static boolean checkIfAttribute(String attribute)
	{
		return attributeMap.containsKey(attribute);
	}
	
	/**
	 * making it synchronized because many tests
	 * run form eclipse start multiple instances of context service 
	 * nodes sharing this same static class.
	 */
	public static synchronized void initialize()
	{
	}
	
	/**
	 * Mainly used for testing.
	 * @param givenMap
	 */
	public static synchronized void initializeGivenMapAndList
				(HashMap<String, AttributeMetaInfo> givenMap, List<String> givenList)
	{
	}
	
	/**
	 * converts String to given datatype
	 * @return
	 */
	public static Object convertStringToDataType(String value, String dataType)
	{
		return null;
	}
	
	/**
	 * Compare first and second argument and returns true if
	 * first argument is smaller than the second argument.
	 * @return
	 */
	public static boolean compareTwoValues(String lowerValue, String upperValue, String dataType)
	{
		return false;
	}
	
	/**
	 * Returns true if two intervals overlap.
	 * @param interval1
	 * @return
	 */
	public static boolean checkOverlapOfTwoIntervals( AttributeValueRange interval1, 
			AttributeValueRange interval2, String dataType )
	{
		return false;
	}
	
	
	public static Object convertStringToDataTypeForMySQL(String value, String dataType)
	{
		return null;
	}
	
	
	public static List<RangePartitionInfo> partitionDomain( int numPartitions, 
			String minValue, String maxValue, String dataType )
	{
		return null;
	}
	
	/**
	 * Returns next String in lexicographic order
	 * @return
	 */
	public static String getNextStringInLexicographicOrder(String inputStr)
	{
		return null;
	}
	
	/**
	 * returns a minimum string of the specified length
	 * @param strLength
	 * @return
	 */
	public static String getMinimumStringOfArgLength(int strLength)
	{
		return null;
	}
	
	/**
	 * returns a minimum string of the specified length
	 * @param strLength
	 * @return
	 */
	public static String getMaximumStringOfArgLength(int strLength)
	{
		return null;
	}
	
	public static Vector<String> getAllAttributes()
	{
		return null;
	}
	
	public static class RangePartitionInfo
	{
		public final int partitionNum;
		public final AttributeValueRange attrValRange;
		
		public RangePartitionInfo( int partitionNum, AttributeValueRange attrValRange )
		{
			this.partitionNum = partitionNum;
			this.attrValRange = attrValRange;
		}
	}
	
	public static void main(String [] args)
	{
	}
}