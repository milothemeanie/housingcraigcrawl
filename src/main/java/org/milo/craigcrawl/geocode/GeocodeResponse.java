package org.milo.craigcrawl.geocode;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GeocodeResponse")
public class GeocodeResponse
{
	private String status;
	
	private List<Result> result = new ArrayList<>();

	/**
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status)
	{
		this.status = status;
	}

	/**
	 * @return the result
	 */
	public List<Result> getResult()
	{
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(List<Result> result)
	{
		this.result = result;
	}
}
