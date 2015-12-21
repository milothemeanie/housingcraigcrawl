package org.milo.craigcrawl;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AddressComponents
{
	private String long_name;

	private String short_name;
	
	private List<String> type;

	/**
	 * @return the long_name
	 */
	public String getLong_name()
	{
		return long_name;
	}

	/**
	 * @param long_name the long_name to set
	 */
	public void setLong_name(String long_name)
	{
		this.long_name = long_name;
	}

	/**
	 * @return the short_name
	 */
	public String getShort_name()
	{
		return short_name;
	}

	/**
	 * @param short_name the short_name to set
	 */
	public void setShort_name(String short_name)
	{
		this.short_name = short_name;
	}

	/**
	 * @return the type
	 */
	public List<String> getType()
	{
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(List<String> type)
	{
		this.type = type;
	}


}
