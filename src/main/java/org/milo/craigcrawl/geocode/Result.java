package org.milo.craigcrawl.geocode;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.milo.craigcrawl.AddressComponents;

@XmlRootElement(name="result")
public class Result
{
	private String status;

	private List<String> type =  new ArrayList<>();
	
	private String formatted_address;

	private List<AddressComponents> address_component = new ArrayList<>();;

	/**
	 * @return the formatted_address
	 */
	public String getFormatted_address()
	{
		return formatted_address;
	}

	/**
	 * @param formatted_address the formatted_address to set
	 */
	public void setFormatted_address(String formatted_address)
	{
		this.formatted_address = formatted_address;
	}


	/**
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status)
	{
		this.status = status;
	}

	/**
	 * @return the type
	 */
	public List<String> getType()
	{
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(List<String> type)
	{
		this.type = type;
	}

	/**
	 * @return the address_component
	 */
	public List<AddressComponents> getAddress_component()
	{
		return address_component;
	}

	/**
	 * @param address_component the address_component to set
	 */
	public void setAddress_component(List<AddressComponents> address_component)
	{
		this.address_component = address_component;
	}


}
