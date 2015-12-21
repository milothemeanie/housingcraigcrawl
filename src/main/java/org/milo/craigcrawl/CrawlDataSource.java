package org.milo.craigcrawl;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;

public class CrawlDataSource 
{

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlDataSource.class);

    private static final CrawlDataSource instance = new CrawlDataSource();

    private HikariDataSource dataSource;

    private CrawlDataSource()
    {
        init();
    }

    private void init()
    {
        try
        {

            LOGGER.info("Creating Data Source");
            
            Class.forName("org.postgresql.Driver");

            dataSource = new HikariDataSource();

            dataSource.setUsername("postgres");
            dataSource.setPassword("dacoolbeans");
            dataSource.setJdbcUrl("jdbc:postgresql://192.168.0.13:5444/craig");
            dataSource.setMaximumPoolSize(25);
            dataSource.setMinimumIdle(1);

        }
        catch (final Throwable t)
        {
            LOGGER.error("Error in intializing the Datasrouce", t);
        }
    }

    public Connection getConnection() throws SQLException
    {
		return dataSource.getConnection();
    	
    }

    public HikariDataSource getDataSource()
    {
        return dataSource;
    }

    public static CrawlDataSource getInstance()
    {
        return instance;
    }
	
}
