package org.milo.craigcrawl;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlJob implements Job
{
	
	private static final String baseurl = "https://oklahomacity.craigslist.org";

	// private final ThreadPoolExecutor executorPool = new ThreadPoolExecutor(1,
	// 5, 30,TimeUnit.SECONDS, new
	// ArrayBlockingQueue<Runnable>(1000),Executors.defaultThreadFactory());
	private final ExecutorService executorPool = Executors
			.newFixedThreadPool(10);

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CrawlJob.class);


	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{
		
		LOGGER.info("Starting Craig Crawl...");
		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		
		try(final CloseableHttpClient client = HttpClients.custom()
				.setConnectionManager(cm).build())
		{
			URI uri = URI.create(baseurl + "/search/apa");

			long starttime = Calendar.getInstance().getTimeInMillis();
			URI link = parseAPA(client, uri);
			while (link != null)
			{
				link = parseAPA(client, link);
			}

			for (Housing housing : unreferencedCoordinates())
			{
				executorPool.execute(new ExtractCoordinates(housing, client));
			}

			executorPool.shutdown();

			while (!executorPool.isTerminated())
			{
			}
			long endtime = Calendar.getInstance().getTimeInMillis();
			LOGGER.info("RunTime:" + (endtime - starttime) / 1000);

		} catch (IOException | SQLException e)
		{
			LOGGER.error("Exception while scheduling work", e);
		}
	}
	
	
	
	private URI parseAPA(final CloseableHttpClient client, final URI link)
			throws ClientProtocolException, IOException
	{
		final HttpGet page = new HttpGet(link);
		try (CloseableHttpResponse response = client.execute(page))
		{
			String html = EntityUtils.toString(response.getEntity());
			Document doc = Jsoup.parse(html);

			for (final Element inputElement : doc.getElementsByClass("row"))
			{
				executorPool.execute(new Scrape(inputElement, client));
			}

			URI nextPage = null;
			for (final Element element : doc.select("a.button.next"))
			{
				if (StringUtils.isNotBlank(element.attr("href")))
				{
					nextPage = URI.create(baseurl + element.attr("href"));
					break;
				}
			}
			return nextPage;
		}
	}

	public List<Housing> unreferencedCoordinates() throws SQLException
	{
		final List<Housing> houseList = new ArrayList<>();

		final StringBuilder query = new StringBuilder();
		query.append("select id, laditude, longitude ");
		query.append("from homerent where longitude is not null and laditude is not null ");
		query.append("and attempted = false and invalid = false");

		try (Connection con = CrawlDataSource.getInstance().getConnection();
				Statement statement = con.createStatement())
		{
			statement.execute(query.toString());
			final ResultSet rs = statement.getResultSet();

			while (rs.next())
			{
				final Housing house = new Housing();
				house.setId(rs.getLong("id"));
				house.setLaditude(rs.getBigDecimal("laditude"));
				house.setLongitude(rs.getBigDecimal("longitude"));

				houseList.add(house);
			}
			rs.close();
		}
		return houseList;
	}
}
