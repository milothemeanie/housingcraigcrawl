package org.milo.craigcrawl;

import java.math.BigDecimal;
import java.util.Date;

public class Housing
{
	private Double price;

	private Integer bedroom;
	
	private Integer squareft;

	private Long id;

	private Date datetime;
	
	private Long repostid;
	
	private String address;
	
	private BigDecimal laditude;
	
	private BigDecimal longitude;
	
	private boolean catallowed;
	
	private boolean dogallowed;
	
	private boolean invalid;
	
	private Integer locationid;

	/**
	 * @return the price
	 */
	public Double getPrice()
	{
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(Double price)
	{
		this.price = price;
	}

	/**
	 * @return the bedroom
	 */
	public Integer getBedroom()
	{
		return bedroom;
	}

	/**
	 * @param bedroom the bedroom to set
	 */
	public void setBedroom(Integer bedroom)
	{
		this.bedroom = bedroom;
	}

	/**
	 * @return the squareft
	 */
	public Integer getSquareft()
	{
		return squareft;
	}

	/**
	 * @param squareft the squareft to set
	 */
	public void setSquareft(Integer squareft)
	{
		this.squareft = squareft;
	}

	/**
	 * @return the id
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @return the datetime
	 */
	public Date getDatetime()
	{
		return datetime;
	}

	/**
	 * @param datetime the datetime to set
	 */
	public void setDatetime(Date datetime)
	{
		this.datetime = datetime;
	}

	/**
	 * @return the repostid
	 */
	public Long getRepostid()
	{
		return repostid;
	}

	/**
	 * @param repostid the repostid to set
	 */
	public void setRepostid(Long repostid)
	{
		this.repostid = repostid;
	}

	/**
	 * @return the address
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}

	/**
	 * @return the laditude
	 */
	public BigDecimal getLaditude()
	{
		return laditude;
	}

	/**
	 * @param laditude the laditude to set
	 */
	public void setLaditude(BigDecimal laditude)
	{
		this.laditude = laditude;
	}

	/**
	 * @return the longitude
	 */
	public BigDecimal getLongitude()
	{
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(BigDecimal longitude)
	{
		this.longitude = longitude;
	}

	/**
	 * @return the catallowed
	 */
	public boolean isCatallowed()
	{
		return catallowed;
	}

	/**
	 * @param catallowed the catallowed to set
	 */
	public void setCatallowed(boolean catallowed)
	{
		this.catallowed = catallowed;
	}

	/**
	 * @return the dogallowed
	 */
	public boolean isDogallowed()
	{
		return dogallowed;
	}

	/**
	 * @param dogallowed the dogallowed to set
	 */
	public void setDogallowed(boolean dogallowed)
	{
		this.dogallowed = dogallowed;
	}

	/**
	 * @return the invalid
	 */
	public boolean isInvalid()
	{
		return invalid;
	}

	/**
	 * @param invalid the invalid to set
	 */
	public void setInvalid(boolean invalid)
	{
		this.invalid = invalid;
	}

	/**
	 * @return the locationid
	 */
	public Integer getLocationid()
	{
		return locationid;
	}

	/**
	 * @param locationid the locationid to set
	 */
	public void setLocationid(Integer locationid)
	{
		this.locationid = locationid;
	}


}
