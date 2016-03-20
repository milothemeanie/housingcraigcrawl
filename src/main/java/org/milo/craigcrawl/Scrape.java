package org.milo.craigcrawl;

import static org.apache.commons.lang3.StringUtils.remove;
import static org.apache.commons.lang3.math.NumberUtils.createDouble;
import static org.apache.commons.lang3.math.NumberUtils.createInteger;
import static org.apache.commons.lang3.math.NumberUtils.createLong;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.milo.craigcrawl.utils.CrawlDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

public class Scrape implements Runnable
{
	private static final String baseurl = "https://oklahomacity.craigslist.org";

	private Element element;

	private CloseableHttpClient client;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Scrape.class);

	Scrape(final Element element, final CloseableHttpClient client)
	{
		this.element = element;
		this.client = client;
	}

	private String findElementValue(final Elements elementList)
	{

		String value = null;
		if (elementList.size() > 0)
		{
			Element housingelement = elementList.get(0);
			value = housingelement.text();
		}
		return value;

	}

	private String findElementAttribute(final Elements elementList,
			final String attribute)
	{

		String value = null;
		if (elementList.size() > 0)
		{
			Element housingelement = elementList.get(0);
			value = housingelement.attr(attribute);
		}
		return value;

	}

	private Integer convertSize(final List<String> sizeList,
			final String remove, final int index)
	{
		Integer sizeInt = null;
		if (sizeList.size() >= index + 1)
		{
			if (StringUtils.contains(sizeList.get(index), remove))
			{
				sizeInt = createInteger(remove(
						StringUtils.trim(sizeList.get(index)), remove));
			}
		}
		return sizeInt;

	}

	private void checkInnerLabel(final Housing housing, final String innerLink)
			throws ClientProtocolException, IOException
	{
		final HttpGet page = new HttpGet(baseurl + innerLink);

		try (final CloseableHttpResponse response = client.execute(page))
		{
			final String html = EntityUtils.toString(response.getEntity());
			final Document doc = Jsoup.parse(html);
			final Element mapelement = doc.getElementById("map");

			if (mapelement != null)
			{
				housing.setLaditude(NumberUtils.createBigDecimal(mapelement
						.attr("data-latitude")));
				housing.setLongitude(NumberUtils.createBigDecimal(mapelement
						.attr("data-longitude")));
			}

			final Elements catElement = doc
					.getElementsContainingText("cats are OK - purrr");

			if (!catElement.isEmpty())
			{
				housing.setCatallowed(true);
			} else
			{
				housing.setCatallowed(false);
			}

			final Elements dogElement = doc
					.getElementsContainingText("dogs are OK - wooof");

			if (!dogElement.isEmpty())
			{
				housing.setDogallowed(true);
			} else
			{
				housing.setDogallowed(false);
			}

			if (housing.getSquareft() == null)
			{
				final Element postingbody = doc.getElementById("postingbody");

				innerLinkAttempt(postingbody, "sq ft", housing, Direction.left);

				if (housing.getSquareft() == null)
				{
					innerLinkAttempt(postingbody, "Sq. Ft.", housing,
							Direction.left);
				}

				if (housing.getSquareft() == null)
				{
					innerLinkAttempt(postingbody, "sqft", housing,
							Direction.left);
				}

				if (housing.getSquareft() == null)
				{
					innerLinkAttempt(postingbody, "Square Feet:", housing,
							Direction.right);
				}

			}
		}

	}

	private void innerLinkAttempt(final Element postingbody,
			final String dictionary, final Housing housing,
			final Direction direction)
	{
		final Elements element = postingbody
				.getElementsContainingOwnText(dictionary);

		if (!element.isEmpty())
		{
			String a = element.text();
			int startindex = 0;
			int endindex = 0;
			if (direction == Direction.left)
			{
				endindex = StringUtils.indexOfIgnoreCase(a, dictionary);
				if (endindex > 5)
				{
					startindex = endindex - 5;
				}
			}

			if (direction == Direction.right)
			{
				startindex = StringUtils.indexOfIgnoreCase(a, dictionary);
				startindex = +dictionary.length() + 1;
				if (startindex < a.length() + 1)
				{
					endindex = startindex + 5;
				}
			}

			String b = StringUtils.trim(StringUtils.removePattern(
					StringUtils.substring(a, startindex, endindex), "[^0-9]"));

			try
			{
				housing.setSquareft(Integer.parseInt(b));
			} catch (NumberFormatException e)
			{
				LOGGER.warn("Unable to format square feet");
			}

		}
	}

	private boolean alreadyInserted(final Housing housing) throws SQLException
	{
		boolean alreadyinserted = false;
		try (Connection con = CrawlDataSource.getInstance().getConnection();
				Statement statement = con.createStatement())
		{
			String idquery = "select count(1) as posted from homerent where id = {id} or repostid={repostid} "
					+ "or (price = {Price} and titlelocation = '{TitleLocation}' and bedroom = {bedroom})";

			idquery = idquery.replace("{id}", Long.toString(housing.getId()));
			idquery = idquery.replace(
					"{repostid}",
					((housing.getRepostid() == null) ? "0" : Long
							.toString(housing.getRepostid())));
			idquery = idquery.replace(
					"{Price}",
					((housing.getPrice() == null) ? "0" : Double
							.toString(housing.getPrice())));
			idquery = idquery.replace(
					"{bedroom}",
					((housing.getBedroom() == null) ? "0" : Integer
							.toString(housing.getBedroom())));
			idquery = idquery.replace(
					"{TitleLocation}",
					((housing.getAddress() == null) ? "null" : housing
							.getAddress()));

			statement.execute(idquery);
			ResultSet rs = statement.getResultSet();
			rs.next();
			int posted = rs.getInt("posted");
			rs.close();

			if (posted > 0)
			{
				alreadyinserted = true;
			}

		}

		return alreadyinserted;

	}

	private void syncHousing(final Housing housing) throws SQLException
	{
		validRecord(housing);

		try (Connection con = CrawlDataSource.getInstance().getConnection();
				Statement statement = con.createStatement())
		{
			final StringBuilder query = new StringBuilder();

			query.append("insert into homerent");
			query.append("(id,datetime,repostid,bedroom,squareft,catokay,dogokay,price,");
			query.append("longitude,laditude,titlelocation,invalid)");
			query.append("values(" + housing.getId() + ",");
			query.append("'" + housing.getDatetime() + "',");
			query.append(housing.getRepostid() + ",");
			query.append(housing.getBedroom() + ",");
			query.append(housing.getSquareft() + ",");
			query.append(housing.isCatallowed() + ",");
			query.append(housing.isDogallowed() + ",");
			query.append(housing.getPrice() + ",");
			query.append(housing.getLongitude() + ",");
			query.append(housing.getLaditude() + ",");
			query.append("'" + housing.getAddress() + "',");
			query.append(housing.isInvalid() + ")");

			statement.executeUpdate(query.toString());

		}
	}

	private boolean validRecord(final Housing housing)
	{

		if (housing.getPrice() == null
				|| (new Double(200).compareTo(housing.getPrice()) > 0)
				|| (new Double(10000).compareTo(housing.getPrice()) < 0))
		{
			housing.setInvalid(true);
		} else
		{
			housing.setInvalid(false);
		}

		return false;

	}

	enum Direction
	{

		right(), left();

	}

	@Override
	public void run()
	{
		
		final Housing housing = new Housing();
		housing.setId(createLong(element.attr("data-pid")));

		MDC.put("housingId", element.attr("data-pid"));

		final String repostId = element.attr("data-repost-of");
		if (StringUtils.isNotBlank(repostId))
		{
			housing.setRepostid(createLong(repostId));
		}

		housing.setPrice(createDouble(remove(
				findElementValue(element.getElementsByClass("price")), '$')));

		String size[] = StringUtils.split(
				findElementValue(element.getElementsByClass("housing")), '-');

		if (size != null)
		{
			size[0] = remove(size[0], '/');
			List<String> sizeList = Arrays.asList(size);

			for (int i = 0; i < sizeList.size(); i++)
			{
				if (StringUtils.contains(sizeList.get(i), "br"))
				{
					housing.setBedroom(convertSize(sizeList, "br", i));
				} else if (StringUtils.contains(sizeList.get(i), "ft"))
				{
					housing.setSquareft(convertSize(sizeList, "ft2", i));
				}
			}
		}

		String titlelocation = findElementValue(element
				.getElementsByTag("small"));
		titlelocation = remove(titlelocation, ')');
		titlelocation = remove(titlelocation, '(');
		housing.setAddress(titlelocation);

		final SimpleDateFormat dformat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm");
		String timestampString = findElementAttribute(
				element.getElementsByTag("time"), "datetime");
		try
		{
			final Date timestamp = dformat.parse(timestampString);
			housing.setDatetime(timestamp);
		} catch (ParseException e)
		{
			LOGGER.warn("Unable to unable to parse housing timestamp",e);
		}

		try
		{
			if (!alreadyInserted(housing))
			{
				checkInnerLabel(housing, findElementAttribute(element.getElementsByClass("i"), "href"));
				syncHousing(housing);

				LOGGER.info("Housing Record Inserted: " + housing.getAddress());
				LOGGER.debug(new JSONSerializer()
						.exclude("*.class")
						.transform(new DateTransformer("yyyy/MM/dd hh:mm:ss"),
								"datetime").deepSerialize(housing));
			}
		} catch (SQLException | IOException e)
		{
			LOGGER.error("Failed to create new Housing record",e);
		} 
	}
}
