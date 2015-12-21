package org.milo.craigcrawl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.milo.craigcrawl.geocode.GeocodeResponse;
import org.milo.craigcrawl.geocode.Result;
import org.milo.craigcrawl.utils.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ExtractCoordinates implements Runnable
{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExtractCoordinates.class);

	private Housing coordinate;

	private CloseableHttpClient client;

	ExtractCoordinates(final Housing housing, final CloseableHttpClient client)
	{
		this.coordinate = housing;
		this.client = client;
	}

	@Override
	public void run()
	{
		MDC.put("housingId", String.valueOf(coordinate.getId()));
		try
		{
			final GeocodeResponse response = coordinateLookup(
					coordinate.getLaditude(), coordinate.getLongitude());

			if (StringUtils.equalsIgnoreCase(response.getStatus(), "OK"))
			{

				LOGGER.info("laditude:" + coordinate.getLaditude()
						+ " === longitude:" + coordinate.getLongitude());

				String streetaddress = null;
				final List<Integer> locations = new ArrayList<>();
				for (Result result : response.getResult())
				{
					for (String type : result.getType())
					{
						if (StringUtils.equals(type, "street_address"))
						{
							streetaddress = result.getFormatted_address();
						}
						if (StringUtils.equals(type, "neighborhood"))
						{
							locations.add(addLocation(result));
						}
					}
				}

				try (Connection con = CrawlDataSource.getInstance()
						.getConnection();
						Statement statement = con.createStatement())
				{
					String update = "update homerent set attempted = true ";
					if (StringUtils.isNotBlank(streetaddress))
					{
						update += ", streetaddress ='" + streetaddress + "' ";
					}

					update += "where id =" + coordinate.getId();
					statement.executeUpdate(update);
					for (Integer id : locations)
					{
						final StringBuilder insert = new StringBuilder();
						insert.append("insert into addressneighborhood (locationid, homerentid) ");
						insert.append("values(");
						insert.append(id);
						insert.append(",");
						insert.append(coordinate.getId());
						insert.append(")");

						statement.executeUpdate(insert.toString());
					}
				}
			}

		} catch (Throwable e)
		{
			LOGGER.error("Failed Geocode lookup", e);
		}

	}

	private Integer addLocation(final Result result) throws Throwable
	{

		final String neighborhood = transposeAddressComponent(
				result.getAddress_component(), AddressType.neighborhood);

		Integer exist = null;

		if (StringUtils.isNotBlank(neighborhood))
		{
			final String query = "select id from location where neighborhood = '"
					+ neighborhood + "'";
			try (Connection con = CrawlDataSource.getInstance().getConnection();
					Statement statement = con.createStatement())
			{
				statement.execute(query.toString());
				ResultSet rs = statement.getResultSet();

				if (rs.isBeforeFirst())
				{
					rs.next();
					exist = rs.getInt(1);
				}

				rs.close();

				if (exist == null)
				{
					String insert = "insert into location (neighborhood, city, county, state) "
							+ "values('{neighborhood}', '{city}', '{county}', '{state}')";

					insert = insert.replace("{neighborhood}", neighborhood);
					insert = insert.replace(
							"{city}",
							transposeAddressComponent(
									result.getAddress_component(),
									AddressType.locality));
					insert = insert.replace(
							"{county}",
							transposeAddressComponent(
									result.getAddress_component(),
									AddressType.administrative_area_level_2));
					insert = insert.replace(
							"{state}",
							transposeAddressComponent(
									result.getAddress_component(),
									AddressType.administrative_area_level_1));

					statement.executeUpdate(insert,
							Statement.RETURN_GENERATED_KEYS);
					rs = statement.getGeneratedKeys();

					rs.next();
					exist = rs.getInt(1);
					rs.close();
				}
			}

			if (exist == null)
				exist = 0;
		}

		return exist;
	}

	private String transposeAddressComponent(
			final List<AddressComponents> addresscomponents,
			final AddressType type) throws Throwable
	{
		for (AddressComponents component : addresscomponents)
		{
			for (String comtype : component.getType())
			{
				if (StringUtils.equals(comtype, type.toString()))
				{
					LOGGER.info(component.getLong_name());
					return component.getLong_name();
				}
			}
		}
		return StringUtils.EMPTY;
	}

	public GeocodeResponse coordinateLookup(final BigDecimal laditude,
			final BigDecimal longitude) throws IOException
	{
		GeocodeResponse geocode = new GeocodeResponse();

		HttpGet page = new HttpGet(
				"http://maps.googleapis.com/maps/api/geocode/xml?latlng="
						+ laditude + "," + longitude + "&sensor=true");

		try (CloseableHttpResponse response = client.execute(page))
		{
			geocode = XmlUtils.load(EntityUtils.toString(response.getEntity()),
					GeocodeResponse.class);

		} catch (JAXBException e)
		{
			LOGGER.error("Failed to serliaze GeocodeResponse", e);
		}
		return geocode;

	}

	public enum AddressType
	{
		neighborhood("neighborhood"), locality("locality"), administrative_area_level_2(
				"administrative_area_level_2"), administrative_area_level_1(
				"administrative_area_level_1");

		private final String name;

		private AddressType(String s)
		{
			name = s;
		}

		public boolean equalsName(String otherName)
		{
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString()
		{
			return this.name;
		}
	}

}
