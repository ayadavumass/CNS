package edu.umass.cs.contextservice.attributeInfo;

import java.util.Random;

import edu.umass.cs.contextservice.regionmapper.helper.AttributeValueRange;

public class AttributeMetaInfo
{
	private final String attributeName;
	
	// irrespective of attribute datatype
	// min and max values ae stored in string type
	// and are converted to the required type on fly
	private final String minValue;
	private final String maxValue;
	
	// used as default value in hyperspace if none is specified.
	public final String defaultValue;
	
	private final String dataType;
	
	
	// true if the default value is lower than the minimum 
	// value. False if the default value is higher than the upper value.
	// default value can never be in between the minimum and maximum value.
	private boolean isLowerValDefault;
	
	// range size, which is typically maxValue-minValue for Int, Long and Double data type.
	// FIXME: not sure what this parameter's value will be in String data type.
	private final double rangeSize;
	
	public AttributeMetaInfo( String attributeName, String minValue, 
			String maxValue, String dataType )
	{
		this.attributeName = attributeName;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.dataType = dataType;
		
		this.defaultValue = null;
		this.isLowerValDefault = true;
		
		rangeSize = computeRangeSize(minValue, maxValue);
	}
	
	public String getAttrName()
	{
		return this.attributeName;
	}
	
	public String getMinValue()
	{
		return this.minValue;
	}
	
	public String getMaxValue()
	{
		return this.maxValue;
	}
	
	public String getDataType()
	{
		return this.dataType;
	}
	
	public String getDefaultValue()
	{
		return this.defaultValue;
	}
	
	public boolean isLowerValDefault()
	{
		return this.isLowerValDefault;
	}
	
	public double getRangeSize()
	{
		return this.rangeSize;
	}
	
	public String getARandomValue(Random randGenerator)
	{
		return null;
	}
	
	/**
	 * Assigns range size based on the data type.
	 */
	public double computeRangeSize( String lowerBound, String upperBound )
	{
		return -1;
	}
	
	public double computeIntervalToRangeRatio(AttributeValueRange attrValRange)
	{
		return -1;
	}
}