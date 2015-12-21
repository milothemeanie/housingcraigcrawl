package org.milo.craigcrawl.geocode;

public class Location
{
	private String neighborhood;

	private String city;

	private String country;

	private String state;

	/**
	 * @return the neighborhood
	 */
	public String getNeighborhood()
	{
		return neighborhood;
	}

	/**
	 * @param neighborhood the neighborhood to set
	 */
	public void setNeighborhood(String neighborhood)
	{
		this.neighborhood = neighborhood;
	}

	/**
	 * @return the city
	 */
	public String getCity()
	{
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city)
	{
		this.city = city;
	}

	/**
	 * @return the country
	 */
	public String getCountry()
	{
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country)
	{
		this.country = country;
	}

	/**
	 * @return the state
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state)
	{
		this.state = state;
	}
}
