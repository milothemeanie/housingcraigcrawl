package org.milo.craigcrawl;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationCraigCrawl
{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ApplicationCraigCrawl.class);

	public static void main(String[] args)
	{
		ApplicationCraigCrawl crawlit = new ApplicationCraigCrawl();
		crawlit.start();
	}

	public void start()
	{
		final JobDetail job = newJob(CrawlJob.class).withIdentity("CraigJob",
				"Crawl").build();

		final Trigger trigger = newTrigger()
				.withIdentity(new TriggerKey("CraigTrigger", "Crawl"))
				.withSchedule(
						simpleSchedule().withIntervalInMinutes(60)
								.repeatForever()).startNow().build();

		try
		{
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		}
		catch (SchedulerException e)
		{
			LOGGER.error("Error while scheduling job", e);
		}

	}

}
